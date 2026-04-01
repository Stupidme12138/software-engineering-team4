package com.example.libraryms.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OverviewStats {
  private long totalBooks;
  private long enabledBooks;
  private long availableBooks;
  private long activeReaders;
  private long borrowedCount;
  private long totalBorrowRecords;
}

