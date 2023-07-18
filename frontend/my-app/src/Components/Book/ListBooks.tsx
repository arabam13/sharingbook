import { useEffect, useState } from 'react';
import Book from './Book';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

import './MyBooks.scss';
import './ListBooks.scss';
import { BookType } from 'utils/type.js';
import { BASE_URL } from 'utils/request.js';

export default function ListBooks() {
  const [books, setBooks] = useState<BookType[]>([]);
  const history = useNavigate();

  useEffect(() => {
    axios
      .get(`${BASE_URL}/books?status=FREE`)
      .then((response) => {
        //console.log(response);
        setBooks(response?.data);
      })
      .catch((error) => {
        setBooks([]);
        history('/login');
      });
  }, []);

  const borrowBook = (bookId: number) => {
    axios.post(`${BASE_URL}/borrows/${bookId}`, {}).then(() => {
      history('/myBorrows');
    });
  };

  return (
    <div className="container">
      <h1>Livres diponibles</h1>
      <div className="list-container">
        {books.length === 0 ? 'Pas de livres disponibles' : null}
        {books.map((book, key) => (
          <div key={key} className="list-book-container">
            <Book title={book?.title} category={book?.category}></Book>
            <div className="text-center">
              <button
                className="btn btn-primary btn-sm"
                onClick={() => borrowBook(book?.id)}
              >
                Emprunter
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
