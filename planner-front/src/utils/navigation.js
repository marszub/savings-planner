import {useNavigate} from "react-router-dom";

export const navigation = {

  _navigate: useNavigate(),

  navigateError() {
    this._navigate("/error");
  },

  navigate(to) {
    this._navigate(to);
  }
}
