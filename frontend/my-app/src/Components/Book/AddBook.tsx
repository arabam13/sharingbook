import { useState, useEffect, ChangeEvent, FormEvent } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';

import './AddBook.scss';
import { BookType, CategoryType } from 'utils/type.js';
import { BASE_URL } from 'utils/request.js';

export default function AddBook() {
  let { bookId } = useParams();
  const [bookData, setBookData] = useState<
    Pick<BookType, 'title' | 'categoryId'>
  >({
    title: '',
    categoryId: 1,
  });
  const [categoriesData, setCategoriesData] = useState<CategoryType[]>([]);
  const history = useNavigate();

  useEffect(() => {
    axios
      .get(`${BASE_URL}/categories`)
      .then((response) => {
        setCategoriesData(response.data);
        setBookData({
          title: '',
          categoryId: response.data[0].id,
        });
      })
      .then(() => {
        if (bookId) {
          axios.get(`${BASE_URL}/books/${bookId}`).then((response) => {
            setBookData({
              title: response.data.title,
              categoryId: response.data.category.id,
            });
          });
        }
      });
  }, [bookId]);

  const handleChange = (
    event: ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    let name = event.target.name;
    let value = event.target.value;
    setBookData((prevState) => ({ ...prevState, [name]: value }));
  };

  const onSubmit = (event: FormEvent) => {
    if (bookId) {
      event.preventDefault();
      axios
        .put(`${BASE_URL}/books/${bookId}`, {
          ...bookData,
        })
        .then(() => {
          //rediriger vers myBooks
          history('/myBooks');
        });
    } else {
      event.preventDefault();
      axios
        .post(`${BASE_URL}/books`, {
          ...bookData,
        })
        .then(() => {
          //rediriger vers myBooks
          history('/myBooks');
        });
    }
  };

  return (
    <div className="container-add-book">
      <h1>{bookId ? 'Modifier' : 'Ajouter'} un livre</h1>
      <form onSubmit={onSubmit}>
        <div>
          <label>Nom du livre</label>
          <input
            name="title"
            type="text"
            value={bookData.title}
            onChange={handleChange}
            className="form-control"
          ></input>
        </div>
        <div>
          <label>Catégorie du livre</label>
          <select
            name="categoryId"
            value={bookData.categoryId}
            onChange={handleChange}
            className="form-select"
          >
            {categoriesData.map((category) => (
              <option value={category.id} key={category.id}>
                {category.label}
              </option>
            ))}
          </select>
        </div>
        <div className="container-submit">
          <input
            type="submit"
            value="Valider"
            className="btn btn-primary"
          ></input>
        </div>
      </form>
    </div>
  );
}
