import { FC, useEffect, useState } from 'react';
import { Navigate, Route, Routes, useNavigate } from 'react-router-dom';
import axios, { AxiosRequestConfig } from 'axios';
import AddBook from 'Components/Book/AddBook';
import AddUser from 'Components/User/AddUser';
import ListBooks from 'Components/Book/ListBooks';
import MyBooks from 'Components/Book/MyBooks';
import Login from 'Components/User/Login';
import Header from 'Header';
import MyBorrows from 'Components/Borrow/MyBorrows';
import Spinner from 'react-bootstrap/Spinner';
import 'bootstrap/dist/css/bootstrap.min.css';

import 'App.scss';
import { UserInfoType } from 'utils/type.js';
import { BASE_URL } from 'utils/request.js';

export const AUTH_TOKEN_KEY = 'jhi-authenticationToken';

const UserConnected: FC<UserInfoType> = ({ userInfo, setUserInfo }) => {
  const history = useNavigate();
  useEffect(() => {
    axios
      .get(`${BASE_URL}/isConnected`)
      .then((response) => {
        setUserInfo(response?.data);
      })
      .catch((error) => {
        setUserInfo('');
      });
  }, [setUserInfo]);

  return (
    <>{userInfo && <Header userInfo={userInfo} setUserInfo={setUserInfo} />}</>
  );
};

function App() {
  const [userInfo, setUserInfo] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    axios.interceptors.request.use(
      (config: AxiosRequestConfig) => {
        const token = localStorage.getItem(AUTH_TOKEN_KEY);
        if (token) {
          if (config.headers) {
            config.headers.Authorization = `Bearer ${token}`;
          }
        }
        setLoading(true);
        return config;
      },
      (error) => {
        setLoading(false);
      }
    );

    axios.interceptors.response.use(
      function (response) {
        setLoading(false);
        return response;
      },
      (error) => {
        setLoading(false);
      }
    );
  }, []);

  return (
    <div id="page">
      {loading && (
        <div className="background-spinner">
          <div className="spinner">
            <Spinner animation="grow" variant="light" />
          </div>
        </div>
      )}
      <UserConnected userInfo={userInfo} setUserInfo={setUserInfo} />
      <div id="content">
        <Routes>
          <Route path="listBooks" element={<ListBooks />} />
          <Route path="myBooks" element={<MyBooks />} />
          <Route path="addBook" element={<AddBook />} />
          <Route path="addBook/:bookId" element={<AddBook />} />
          <Route path="myBorrows" element={<MyBorrows />} />
          <Route
            path="login"
            element={<Login userInfo={userInfo} setUserInfo={setUserInfo} />}
          />
          <Route
            path="addUser"
            element={<AddUser userInfo={userInfo} setUserInfo={setUserInfo} />}
          />
          <Route
            path="*"
            element={
              userInfo ? (
                <Navigate to="listBooks" replace={true} />
              ) : (
                <Navigate to="login" replace={true} />
              )
            }
          />
        </Routes>
      </div>
    </div>
  );
}
export default App;
