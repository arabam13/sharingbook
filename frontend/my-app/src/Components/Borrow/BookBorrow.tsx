import { BorrowType } from 'utils/type.js';
import 'Components/Book/Book.scss';

export default function Book({
  book,
  lender,
  askDate,
  closeDate,
}: Omit<BorrowType, 'id'>) {
  const displaydate = (dateStr: Date) => {
    const newDate = new Date(dateStr);
    return newDate.toLocaleDateString('fr-FR');
  };

  return (
    <div className="book">
      <div className="book-image">
        <img
          src={new URL('/src/assets/book.png', import.meta.url).href}
          alt="Book"
        />
      </div>
      <div>Titre : {book.title}</div>
      <div>Catégorie: {book.category.label}</div>
      {lender && (
        <div>Prêteur: {lender.firstName + ' ' + lender.firstName}</div>
      )}
      {askDate && <div>Date demande: {displaydate(askDate)}</div>}
      {closeDate && <div>Date cloture: {displaydate(closeDate)}</div>}
    </div>
  );
}
