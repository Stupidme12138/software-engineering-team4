package com.example.libraryms.common;

public final class ErrorCodes {
  private ErrorCodes() {}

  public static final int UNAUTHORIZED = 401;
  public static final int FORBIDDEN = 403;
  public static final int BAD_REQUEST = 400;
  public static final int NOT_FOUND = 404;

  public static final int USERNAME_OR_PASSWORD_INVALID = 10001;
  public static final int USER_DISABLED = 10002;

  public static final int DATA_NOT_FOUND = 20001;
  public static final int DATA_CONFLICT = 20002;

  public static final int BOOK_NOT_AVAILABLE = 30001;
  public static final int READER_CANCELLED = 30002;
  public static final int BORROW_STATE_INVALID = 30003;
}

