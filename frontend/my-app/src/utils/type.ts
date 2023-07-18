export type UserInfoType = {
  userInfo: string;
  setUserInfo: React.Dispatch<React.SetStateAction<string>>;
};

export type StoreType = {
  userData: {
    email: string;
    firstName: string;
    lastName: string;
    password: string;
  };
  showModal: boolean;
};

export type CategoryType = {
  id: number;
  label: string;
};

export type BookType = {
  id: number;
  title: string;
  categoryId: CategoryType['id'];
  category: CategoryType;
};

export type BorrowType = {
  id: number;
  book: BookType;
  lender: Pick<StoreType['userData'], 'firstName' | 'lastName'>;
  askDate: Date;
  closeDate: Date;
};
