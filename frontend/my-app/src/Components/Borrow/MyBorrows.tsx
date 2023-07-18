import BookBorrow from 'Components/Borrow/BookBorrow';
import axios from 'axios';
import './MyBorrows.scss';
import { BorrowType } from 'utils/type.js';
import { useEffect, useState } from 'react';
import { BASE_URL } from 'utils/request.js';
import { useNavigate } from 'react-router-dom';

export default function MyBorrows() {
  const [myBorrows, setMyBorrows] = useState<BorrowType[]>([]);
  const history = useNavigate();
  const getMyBorrows = () => {
    axios
      .get(`${BASE_URL}/borrows`)
      .then((response) => {
        setMyBorrows(response.data);
      })
      .catch((error) => {
        setMyBorrows([]);
        history('/login');
      });
  };

  useEffect(() => {
    getMyBorrows();
  }, []);

  const closeBorrow = (borrowId: number) => {
    axios.put(`${BASE_URL}/borrows/${borrowId}`).then((response) => {
      getMyBorrows();
    });
  };

  return (
    <div className="container">
      <h1>Mes emprunts</h1>
      <div className="list-container">
        {myBorrows.map((borrow, key) => {
          return (
            <div className="borrow-container" key={key}>
              <BookBorrow
                book={borrow.book}
                lender={borrow.lender}
                askDate={borrow.askDate}
                closeDate={borrow.closeDate}
              ></BookBorrow>
              <div className="text-center">
                {borrow.closeDate ? (
                  ''
                ) : (
                  <button
                    className="btn btn-primary btn-sm"
                    onClick={() => closeBorrow(borrow.id)}
                  >
                    Clore
                  </button>
                )}
              </div>
            </div>
          );
        })}
      </div>
      {myBorrows.length === 0 ? <div>Vous n'avez pas d'emprunt</div> : null}
    </div>
  );
}
