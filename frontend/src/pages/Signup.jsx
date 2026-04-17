import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import styles from './Auth.module.css';
import logo from "../assets/Logo.png";
import { API_BASE } from '../config'

function Signup() {
  const [form, setForm] = useState({
    username: "",
    password: "",
    confirmPassword: "",
  });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSignup = async (e) => {
    e.preventDefault();
    setError(null);

    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(`${API_BASE}/api/users/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          username: form.username,
          password: form.password,
          balance: 1000,
        }),
      });

      if (!response.ok) {
        throw new Error("Username already taken or registration failed");
      }

      navigate("/login");
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
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
          <label className={styles.label}>Username</label>
          <input
            className={styles.input}
            name="username"
            type="text"
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

          {error && (
            <p style={{ color: "red", fontSize: "14px", marginBottom: "12px" }}>
              {error}
            </p>
          )}

          <button className={styles["login-btn"]} disabled={loading}>
            {loading ? "Creating Account..." : "Create Account"}
          </button>

          <button
            type="button"
            className={styles["signup-btn"]}
            onClick={() => navigate("/login")}
          >
            ALREADY HAVE AN ACCOUNT?
          </button>
        </form>
      </div>
    </div>
  );
}

export default Signup;