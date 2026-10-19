# Clinic Manager: Efficient Data Structures

A high-performance clinic management system built in Java. This project demonstrates the practical application of advanced data structures to achieve strict $O(\log N)$ and $O(1)$ time complexities for complex querying and sorting operations.

## Overview
The system manages doctors, their active loads (patient counts), and their individual patient queues. It allows for dynamic updates (patients entering/leaving) and range queries (e.g., calculating the average load of doctors within a specific patient-count range).

## Core Data Structures
To meet strict performance requirements, standard Java collections (`java.util.*`) were entirely avoided. The system is built from scratch using:

*   **Leaf-Oriented 2-3 Tree:** Used for $O(\log N)$ lookups, insertions, and deletions by ID.
*   **Augmented 2-3 Tree:** A secondary 2-3 tree sorts doctors by load. Internal nodes are augmented with `subtreeSize` and `subtreeLoadSum` to process rank and average range queries in $O(\log N)$ time.
*   **Doubly Linked List:** Each doctor maintains a custom doubly linked list of patients, allowing $O(1)$ removal of a patient from the middle of the waiting queue.

## Key Features & Time Complexities
*   **Doctor / Patient Registration:** $O(\log N)$
*   **Patient Queue Management:** $O(\log N)$ for tree updates + $O(1)$ for queue adjustments.
*   **Range Queries (Rank & Average):** $O(\log N)$ using augmented node statistics.

## How to Run
1. Clone the repository.
2. Compile the files: `javac *.java`
3. Execute your main testing class (e.g., `java Main`)
