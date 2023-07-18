import { ChangeEvent, FormEvent, useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './AddUser.scss';
import { AUTH_TOKEN_KEY } from 'App';
import SimpleModal from 'SimpleModal';
import { StoreType, UserInfoType } from 'utils/type.js';
import { BASE_URL } from 'utils/request.js';

export default function AddUser({ userInfo, setUserInfo }: UserInfoType) {
  const [store, setStore] = useState<StoreType>({
    userData: { email: '', firstName: '', lastName: '', password: '' },
    showModal: false,
  });
  const history = useNavigate();

  useEffect(() => {
    if (userInfo) {
      history('listBooks');
    }
  }, [userInfo]);

  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    let currentState: StoreType['userData'] = { ...store.userData };
    let name = event.target.name;
    let value = event.target.value;
    setStore((prevState) => ({
      ...prevState,
      userData: { ...currentState, [name]: value },
    }));
  };

  const handleCloseModal = () => {
    setStore((prevState) => ({ ...prevState, showModal: false }));
  };

  const onSubmit = (event: FormEvent) => {
    event.preventDefault();
    axios
      .post(`${BASE_URL}/users`, {
        ...store.userData,
      })
      .then((response) => {
        // console.log(response.data);
        // const bearerToken = response.headers['Authorization'];
        const bearerToken = response.data['token'];
        if (bearerToken && bearerToken.slice(0, 7) === 'Bearer ') {
          const jwt = bearerToken.slice(7, bearerToken.length);
          localStorage.setItem(AUTH_TOKEN_KEY, jwt);
        }
        setUserInfo(response.data.email);
        history('/myBooks');
      })
      .catch(() => {
        setStore((prevState) => ({ ...prevState, showModal: true }));
      });
  };

  const title =
    store.userData.email &&
    store.userData.firstName &&
    store.userData.lastName &&
    store.userData.password
      ? 'Mail déja utilisé'
      : 'Champ manquant';
  const bodyTxt =
    store.userData.email &&
    store.userData.firstName &&
    store.userData.lastName &&
    store.userData.password
      ? "Cet email est déja utilisé, merci d'en saisir un autre."
      : 'Un ou plusieurs champs sont manquants, veuillez le ou les renseigner.';

  return (
    <>
      <div className="add-user-container">
        <div>
          <h1>M'inscrire</h1>
          <div>
            <form onSubmit={onSubmit}>
              <div>
                <label>email</label>
                <input
                  name="email"
                  type="text"
                  className="form-control"
                  onChange={handleChange}
                />
              </div>
              <div>
                <label>nom</label>
                <input
                  name="lastName"
                  type="text"
                  className="form-control"
                  onChange={handleChange}
                />
              </div>
              <div>
                <label>prenom</label>
                <input
                  name="firstName"
                  type="text"
                  className="form-control"
                  onChange={handleChange}
                />
              </div>
              <div>
                <label>password</label>
                <input
                  name="password"
                  type="password"
                  className="form-control"
                  onChange={handleChange}
                />
              </div>
              <div className="container-valid text-center">
                <input
                  type="submit"
                  value="Valider"
                  className="btn btn-primary"
                />
              </div>
            </form>
          </div>
          <div className="container-back">
            <Link to="/">Retour à l'accueil</Link>
          </div>
        </div>
      </div>
      <SimpleModal
        title={title}
        bodyTxt={bodyTxt}
        handleCloseModal={handleCloseModal}
        showModal={store.showModal}
      ></SimpleModal>
    </>
  );
}
