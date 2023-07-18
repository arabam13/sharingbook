import { ChangeEvent, FormEvent, useEffect, useState } from 'react';
import SimpleModal from 'SimpleModal';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import './Login.scss';
import { AUTH_TOKEN_KEY } from 'App';
import { UserInfoType } from 'utils/type.js';
import { BASE_URL } from 'utils/request.js';

export default function Login({ userInfo, setUserInfo }: UserInfoType) {
  const [store, setStore] = useState({
    userData: { email: '', firstName: '', lastName: '', password: '' },
    showModal: false,
  });
  const history = useNavigate();

  useEffect(() => {
    if (userInfo) {
      history('listbooks');
    }
  }, [userInfo]);

  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    let currentState = { ...store.userData };
    let name = event.target.name;
    let value = event.target.value;
    setStore((prevState) => ({
      ...prevState,
      userData: { ...currentState, [name]: value },
    }));
  };

  const onSubmit = (event: FormEvent) => {
    event.preventDefault();
    axios
      .post(`${BASE_URL}/authenticate`, {
        email: store.userData.email,
        password: store.userData.password,
      })
      .then((response) => {
        // const bearerToken = response?.headers?.authorization;
        const bearerToken = response.data['token'];
        if (bearerToken && bearerToken.slice(0, 7) === 'Bearer ') {
          const jwt = bearerToken.slice(7, bearerToken.length);
          localStorage.setItem(AUTH_TOKEN_KEY, jwt);
        }
        setUserInfo(response.data.email);
        history('/listbooks');
      })
      .catch(() => {
        setStore((prevState) => ({ ...prevState, showModal: true }));
      });
  };

  const handleCloseModal = () => {
    setStore((prevState) => ({ ...prevState, showModal: false }));
  };

  const title = 'Login incorrect';
  const bodyTxt = 'Login ou mot de passe incorrect';
  return (
    <>
      <div className="login-container">
        <div>
          <div style={{ textAlign: 'center' }}>
            <img
              src={new URL('/src/assets/logo.jpg', import.meta.url).href}
              alt="Logo"
            />
          </div>
          <div className="title" style={{ margin: '20px' }}>
            Bienvenue sur Sharingbook app!
          </div>
          <div className="form-container">
            <form onSubmit={onSubmit}>
              <span>Mail: </span>
              <input
                type="text"
                className="form-control"
                name="email"
                onChange={handleChange}
              ></input>
              <span>Passsword: </span>
              <input
                type="password"
                className="form-control"
                name="password"
                onChange={handleChange}
              ></input>
              <div className="text-center button-style">
                <input
                  type="submit"
                  className="btn btn-primary"
                  value="Se connecter"
                />
              </div>
            </form>
          </div>
          <div className="text-center">
            <Link to="/addUser">M'inscrire</Link>
          </div>
        </div>
      </div>

      <SimpleModal
        title={title}
        bodyTxt={bodyTxt}
        handleCloseModal={handleCloseModal}
        showModal={store.showModal}
      />
    </>
  );
}
