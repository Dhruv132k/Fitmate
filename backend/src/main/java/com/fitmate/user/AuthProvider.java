package com.fitmate.user;

/*How a user authenticates. LOCAL = email + password; others = social login */
public enum AuthProvider {
    LOCAL,
    GOOGLE,
    GITHUB
}