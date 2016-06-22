# CSVParser

Application wrote in Scala to manipulate CSV files.

## Implemented operations

- *csv minus csv:* remove from the first CSV the rows with a key existing in the second CSV.
Execution parameters:
csvminuscsv
csv_separator
path_input_csv
path_output_csv
encoding_of_input_and_output_csv
path_second_input_csv
key_position_first_input_csv
key_position_second_input_csv

- *csv intersect csv:* get from the first CSV the rows with a key existing in the second CSV.
Execution parameters:
csvintersect
csv_separator
path_input_csv
path_output_csv
encoding_of_input_and_output_csv
path_second_input_csv
key_position_first_input_csv
key_position_second_input_csv

- *get column:* get a single column from the CSV.
Execution parameters:
getcolumn
csv_separator
path_input_csv
path_output_csv
encoding_of_input_and_output_csv
column_position

- *add column:* add a column to the CSV.
Execution parameters:
getcolumn
csv_separator
path_input_csv
path_output_csv
encoding_of_input_and_output_csv
new_column_position
new_column_value
