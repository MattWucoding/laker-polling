package edu.oswego.cs.lakerpolling.services;

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord;

class CourseListParserService {

    private enum Column {
        EMAIL("Email"),
        LAKER_NET_ID("Username")

        private String header

        private Column(String header) {
            this.header = header
        }

        boolean matches(String header) {
            header.replaceAll("[^A-Za-z]", "").equalsIgnoreCase(this.header)
        }
    }

    private CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader()
    private Map<Column, Integer> columnIndexMap = [(Column.EMAIL): null, (Column.LAKER_NET_ID): null]

    /**
     * Parses the given CSV file as a course list of students. The first row in the CSV file must be a header row, which
     * must contain a header labeled either "Email" or "Username" (case insensitive). For each record in the file the
     * parser will either extract the student's email directly from the "Email" column or will generate it dynamically
     * using the student's Laker NetID extracted from the "Username" column.
     *
     * @param file - a CSV file containing a list of students
     * @return a list of student emails extracted from the CSV file
     */
    List<String> parse(File file) {
        BufferedReader reader = new BufferedReader(new FileReader(file))
        try {
            List<String> emails = new ArrayList<>()
            CSVParser parser = format.parse(reader)
            readAndVerifyHeaders(parser.getHeaderMap())

            for (CSVRecord record : parser.iterator()) {
                String email = parseEmail(record)
                if (email != null && !email.trim().isEmpty()) {
                    emails.add(email)
                }
            }
            return emails
        } finally {
            reader.close()
        }
    }

    private void readAndVerifyHeaders(Map<String,Integer> headerIndexMap) {
        // associate the logical columns with the actual column index in the CSV based on the headers
        for (headerIndexEntry in headerIndexMap.entrySet()) {
            for (column in Column.values()) {
                if (getColumnIndex(column) != null) {
                    continue
                }
                if (column.matches(headerIndexEntry.getKey())) {
                    columnIndexMap.put(column, headerIndexEntry.value)
                    break
                }
            }
        }

        if (getColumnIndex(Column.EMAIL) == null && getColumnIndex(Column.LAKER_NET_ID) == null) {
            throw new IOException("Could not find Email or Laker NetID column")
        }
    }

    private Integer getColumnIndex(Column column) {
        return columnIndexMap.get(column)
    }

    private String parseColumnValue(CSVRecord record, Column column) {
        Integer index = getColumnIndex(column)
        return index != null ? record.get(index) : null
    }

    private String parseEmail(CSVRecord record) {
        String email = parseColumnValue(record, Column.EMAIL)
        return email != null ? email : "${parseColumnValue(record, Column.LAKER_NET_ID)}@oswego.edu"
    }
}
