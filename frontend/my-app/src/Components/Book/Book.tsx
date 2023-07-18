import { BookType } from 'utils/type.js';
import './Book.scss';

export default function Book({
  title,
  category,
}: Omit<BookType, 'id' | 'categoryId'>) {
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
      <div>Titre : {title}</div>
      <div>Catégorie: {category.label}</div>
    </div>
  );
}
