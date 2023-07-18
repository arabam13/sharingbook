package com.udemy.demo.book;

import com.udemy.demo.borrow.Borrow;
import com.udemy.demo.borrow.BorrowRepository;
import com.udemy.demo.dtos.CategoryDetailsDTO;
import com.udemy.demo.user.UserInfo;
import com.udemy.demo.user.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.udemy.demo.dtos.UserDetailsDTO;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class BookController {

    @Autowired
     BookRepository bookRepository;

    @Autowired
     UserRepository userRepository;

    @Autowired
     CategoryRepository categoryRepository;

    @Autowired
     BorrowRepository borrowRepository;


    @GetMapping(value = "/books")
    public ResponseEntity<?> list(@RequestParam(required = false) BookStatus status) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserInfo existingUser = userRepository.findOneByEmail(((UserDetails) principal).getUsername());
        Integer userConnectedId = existingUser.getId();
        List<Book> books;
        if (status != null && status == BookStatus.FREE) {
            books = bookRepository.findByStatusAndAuthorIdNot(status, userConnectedId);
        } else {
            books = bookRepository.findByAuthorId(userConnectedId);
        }

        Map<String, Object> bookMap = new HashMap<>();
        List<Map<String, Object>> booksMap = new ArrayList<>();
        for (Book book : books){
            bookMap.put("id", book.getId());
            bookMap.put("title", book.getTitle());
            bookMap.put("status", book.getStatus().toString());
            UserInfo userCreatorBook = userRepository.findByIdWithoutPassword(book.getAuthorId());
            UserDetailsDTO userCreatorBookDTO = new UserDetailsDTO(userCreatorBook.getFirstName(), userCreatorBook.getLastName());
            bookMap.put("author", userCreatorBookDTO);
            Optional<Category> catgoryOfBook = categoryRepository.findById(Integer.valueOf(book.getCategoryId()));
            CategoryDetailsDTO categoryDTO = new CategoryDetailsDTO(catgoryOfBook.get().getId(), catgoryOfBook.get().getLabel());
            bookMap.put("category", categoryDTO);
            booksMap.add(bookMap);
            bookMap = new HashMap<>();

        }
        
        return new ResponseEntity<>(booksMap, HttpStatus.OK);
    }


    @PostMapping(value = "/books")
    public ResponseEntity<String> create(@RequestBody @Valid Book book) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserInfo existingUser = userRepository.findOneByEmail(((UserDetails) principal).getUsername());
        Integer userConnectedId = existingUser.getId();
        Optional<UserInfo> author = userRepository.findById(userConnectedId);
        if (author.isPresent()) {
            book.setAuthorId(author.get().getId());
        } else {
            return new ResponseEntity<String>("You must provide a valid user", HttpStatus.BAD_REQUEST);
        }

        Optional<Category> category = categoryRepository.findById(book.getCategoryId());
        if (category.isPresent()) {
            book.setCategoryId(category.get().getId());
        } else {
            return new ResponseEntity<String>("You must provide a valid category", HttpStatus.BAD_REQUEST);
        }

        book.setStatus(BookStatus.FREE);
        book.setAuthorId(author.get().getId());
        book.setCategoryId(category.get().getId());
        bookRepository.save(book);

        return new ResponseEntity<>("The book \"" + book.getTitle() + "\" has been created!",  HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/books/{bookId}")
    public ResponseEntity<String> deleteBook(@PathVariable("bookId") String bookId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserInfo existingUser = userRepository.findOneByEmail(((UserDetails) principal).getUsername());
        Integer userConnectedId = existingUser.getId();

        Optional<Book> bookToDelete = bookRepository.findById(Integer.valueOf(bookId));
        if (!bookToDelete.isPresent()) {
            return new ResponseEntity<String>("Book not found", HttpStatus.NOT_FOUND);
        }
        // if authorId equals userConnectedId
        if (bookToDelete.get().getAuthorId() == userConnectedId){

            List<Borrow> borrows = borrowRepository.findByBookId(bookToDelete.get().getId());
            //if there is borrows for this related book
            if (borrows.size() != 0){
                for (Borrow borrow : borrows) {
                    // Not deleting borrowed book
                    if (borrow.getCloseDate() == null && borrow.getBookId() == bookToDelete.get().getId())  {
                        return new ResponseEntity<String>("The book \"" + bookToDelete.get().getTitle() + "\" has been borrowed, can't be deleted !", HttpStatus.CONFLICT);
                    }
                }
            }
            
            bookRepository.deleteById(Integer.valueOf(bookId));
            return new ResponseEntity<String>("The book \"" + bookToDelete.get().getTitle() + "\" has been deleted !",  HttpStatus.OK);
        }

        return new ResponseEntity<String>("You can't delete the book. It doesn't belong to you!",  HttpStatus.FORBIDDEN);


    }


    @PutMapping(value = "/books/{bookId}")
    public ResponseEntity<String> updateBook(@PathVariable("bookId") String bookId, @Valid @RequestBody Book book) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserInfo existingUser = userRepository.findOneByEmail(((UserDetails) principal).getUsername());
        Integer userConnectedId = existingUser.getId();

        Optional<Book> bookDB = bookRepository.findById(Integer.valueOf(bookId));
        if (!bookDB.isPresent()) {
            return new ResponseEntity<>("the book with bookId "+ bookId +" not existing", HttpStatus.NOT_FOUND);
        }
        String OldbookTitle = bookDB.get().getTitle();
        Integer OldbookCategoryId = bookDB.get().getCategoryId();
        Book bookToUpdate = bookDB.get();
        // the pusblisher of book is same than userConnected
        if (bookToUpdate.getAuthorId() == userConnectedId) {
            // the book passed in is different of title stored in DB Book
            if (!book.getTitle().equals(OldbookTitle)){
                bookToUpdate.setTitle(book.getTitle());
            }
            if (book.getCategoryId() != OldbookCategoryId){
                bookToUpdate.setCategoryId(book.getCategoryId());
            }
            if (book.getTitle().equals(OldbookTitle) && book.getCategoryId() == OldbookCategoryId){
                return new ResponseEntity<>("There is not change of book", HttpStatus.OK);

            }
            bookRepository.save(bookToUpdate);
            return new ResponseEntity<>("The book \"" +  OldbookTitle +  "\" has been updated to : " +bookToUpdate.getTitle(), HttpStatus.OK);
        }
        return new ResponseEntity<String>("You can't update the book. It doesn't belong to you!",  HttpStatus.FORBIDDEN);


    }

    @GetMapping("/categories")
    public ResponseEntity<Iterable<Category>> listCategories() {
        Iterable<Category> categories = categoryRepository.findAll();

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/books/{bookId}")
    public ResponseEntity<?> loadBook(@PathVariable("bookId") String bookId) {

        Optional<Book> bookDB = bookRepository.findById(Integer.valueOf(bookId));
        if(!bookDB.isPresent()){
            return new ResponseEntity<String>("Book not found", HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> bookMap = new HashMap<>();
        bookMap.put("id", bookDB.get().getId());
        bookMap.put("title", bookDB.get().getTitle());
        bookMap.put("status", bookDB.get().getStatus());
        UserInfo userCreatorBook = userRepository.findByIdWithoutPassword(bookDB.get().getAuthorId());
        UserDetailsDTO userCreatorBookDTO = new UserDetailsDTO(userCreatorBook.getFirstName(), userCreatorBook.getLastName());
        bookMap.put("author", userCreatorBookDTO);
        Optional<Category> catgoryOfBook = categoryRepository.findById(Integer.valueOf(bookDB.get().getCategoryId()));
        CategoryDetailsDTO categoryDTO = new CategoryDetailsDTO(catgoryOfBook.get().getId(), catgoryOfBook.get().getLabel());
        bookMap.put("category", categoryDTO);


        return new ResponseEntity<Map<String, Object>>(bookMap, HttpStatus.OK);
    }


}