package com.udemy.demo.borrow;

import com.udemy.demo.book.Book;
import com.udemy.demo.book.BookRepository;
import com.udemy.demo.book.BookStatus;
import com.udemy.demo.book.Category;
import com.udemy.demo.book.CategoryRepository;
import com.udemy.demo.dtos.BookDetailsDTO;
import com.udemy.demo.dtos.CategoryDetailsDTO;
import com.udemy.demo.dtos.UserDetailsDTO;
import com.udemy.demo.user.UserInfo;
import com.udemy.demo.user.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class BorrowController {

    @Autowired
     BorrowRepository borrowRepository;

    @Autowired
     BookRepository bookRepository;

    @Autowired
     UserRepository userRepository;

    @Autowired
     CategoryRepository categoryRepository;


    @GetMapping(value ="/borrows")
    public ResponseEntity<List<Map<String, Object>>> list() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserInfo userConnected = userRepository.findOneByEmail(((UserDetails) principal).getUsername());
        // serching for borrows of userConnected
        List<Borrow> borrows = borrowRepository.findByBorrowerId(userConnected.getId());
        
        Map<String, Object> borrowMap = new HashMap<>();
        List<Map<String, Object>> borrowsMap = new ArrayList<>();
        for (Borrow borrow : borrows){
            borrowMap.put("id", borrow.getId());
            UserInfo lender = userRepository.findByIdWithoutPassword(borrow.getLenderId());
            UserDetailsDTO LenderDTO = new UserDetailsDTO(lender.getFirstName(), lender.getLastName());
            borrowMap.put("lender", LenderDTO);
            borrowMap.put("askDate", borrow.getAskDate());
            borrowMap.put("closeDate", borrow.getCloseDate());
            Optional<Book> book = bookRepository.findById(borrow.getBookId());
            Optional<Category> category = categoryRepository.findById(book.get().getCategoryId());
            BookDetailsDTO bookDTO = new BookDetailsDTO(book.get().getId(), book.get().getTitle(), new CategoryDetailsDTO(category.get().getId(), category.get().getLabel()));
            borrowMap.put("book", bookDTO);
            borrowsMap.add(borrowMap);
            borrowMap = new HashMap<>();
        }
        return new ResponseEntity<List<Map<String, Object>>>(borrowsMap,  HttpStatus.OK);
    }

    @PostMapping("/borrows/{bookId}")
    public ResponseEntity<String> create(@PathVariable("bookId") String bookId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserInfo userConnected = userRepository.findOneByEmail(((UserDetails) principal).getUsername());
        Integer userConnectedId = userConnected.getId();
        Optional<UserInfo> borrower = userRepository.findById(userConnectedId);

        Optional<Book> book = bookRepository.findById(Integer.valueOf(bookId));
        if(!book.isPresent()) {
            return new ResponseEntity<String>("book not found", HttpStatus.NOT_FOUND);
        }

        if (borrower.isPresent() && borrower.get().getId() == book.get().getAuthorId()){
            return new ResponseEntity<String>("You can't borrow your own book", HttpStatus.CONFLICT);
        }
            
        if(borrower.isPresent() && book.isPresent() && book.get().getStatus().equals(BookStatus.FREE)) {
            Borrow borrow = new Borrow();
            Book bookEntity = book.get();
            borrow.setBookId(bookEntity.getId());
            borrow.setBorrowerId(borrower.get().getId());
            borrow.setLenderId(bookEntity.getAuthorId());
            borrow.setAskDate(LocalDate.now());
            borrowRepository.save(borrow);

            bookEntity.setStatus(BookStatus.BORROWED);
            bookRepository.save(bookEntity);
            return new ResponseEntity<>("The book \""+bookEntity.getTitle()+"\" has been borrowed.", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Book Not available for borrowing", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/borrows/{borrowId}")
    public ResponseEntity<String> delete(@PathVariable("borrowId") String borrowId) {
        Optional<Borrow> borrow = borrowRepository.findById(Integer.valueOf(borrowId));
        if(borrow.isEmpty()) {
            return new ResponseEntity<>("Borrow not found", HttpStatus.BAD_REQUEST);
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserInfo userConnected = userRepository.findOneByEmail(((UserDetails) principal).getUsername());

        if (borrow.isPresent() && borrow.get().getBorrowerId() != userConnected.getId()){
            return new ResponseEntity<String>("You can't end the loan. It's not your borrow", HttpStatus.CONFLICT);
        }

        Borrow borrowEntity = borrow.get();
        borrowEntity.setCloseDate(LocalDate.now());
        borrowRepository.save(borrowEntity);

        Optional<Book> book = bookRepository.findById(borrowEntity.getBookId());
        if (book.isPresent()){
            book.get().setStatus(BookStatus.FREE);
            bookRepository.save(book.get());

        }

        return new ResponseEntity<>("book \""+ book.get().getTitle() +"\" is now available for borrowing", HttpStatus.OK);
    }

}