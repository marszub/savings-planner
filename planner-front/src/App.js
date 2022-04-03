import { Route, BrowserRouter as Router, Switch } from "react-router-dom";
import SignIn from "./Components/Auth/SignIn";
import SignUp from "./Components/Auth/SignUp";

function App() {
  return (
      <Router>
        <Switch>
          <Route path='/sign-in' component={ SignIn } />
          <Route path='/sign-up' component={ SignUp } />
        </Switch>
      </Router>
  );
}

export default App;
