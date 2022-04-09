import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import SignIn from "./Components/Auth/SignIn";
import SignUp from "./Components/Auth/SignUp";

function App() {

    return (
        <Router>
            <Routes>
                <Route path='/sign-in' element={ <SignIn /> } />
                <Route path='/sign-up' element={ <SignUp /> } />
            </Routes>
        </Router>
  );
}

export default App;
