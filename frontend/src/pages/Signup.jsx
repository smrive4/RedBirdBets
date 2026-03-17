import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import styles from './Auth.module.css';
import logo from "../assets/Logo.png";

function Signup() {
  const [form, setForm] = useState({
    email: "",
    password: "",
    confirmPassword: "",
  });

  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSignup = (e) => {
    e.preventDefault();

    if (form.password !== form.confirmPassword) {
      alert("Passwords do not match");
      return;
    }

    console.log("Signup:", form);
  };

  return (
    <div className={styles["auth-container"]}>
      <div className={styles.left}>
        <Link to="/" className={styles["brand-link"]}>
          <img src={logo} alt="logo" />
          <h1>Redbird Bets</h1>
        </Link>
      </div>

      <div className={styles.right}>
        <h2>Sign Up</h2>

        <form className={styles.form} onSubmit={handleSignup}>
          <label className={styles.label}>Email</label>
          <input
            className={styles.input}
            name="email"
            type="email"
            onChange={handleChange}
            required
          />

          <label className={styles.label}>Password</label>
          <input
            className={styles.input}
            name="password"
            type="password"
            onChange={handleChange}
            required
          />

          <label className={styles.label}>Confirm Password</label>
          <input
            className={styles.input}
            name="confirmPassword"
            type="password"
            onChange={handleChange}
            required
          />

          <button className={styles["login-btn"]}>
            Create Account
          </button>

          <button
            type="button"
            className={styles["signup-btn"]}
            onClick={() => navigate("/")}
          >
            BACK TO LOGIN
          </button>
        </form>
      </div>
    </div>
  );
}

export default Signup;
