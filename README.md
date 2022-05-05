# Batch Import

This batch imports datas from files into Product database.
The framework used is `Spring Batch`.

## The big picture

2 flows are involved in this batch :

- `saveFilesFlow`: This flow reads the temporary files and inserts the content as temporary tables.
  The steps of this flow are executed in a multi-threaded context. Each step will read a file and extract it to a temporary table.
- `insertProductsFlow`: This flow reads and aggregates the temporary tables content and inserts the result to the Product database (to table products)

### Requirement

You need to get JDK 17 version

### Build

    ./gradlew build

### Boot of service

    ./gradlew bootRun

