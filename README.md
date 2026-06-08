# Note Taking Service

A minimal Spring Boot service for writing a note and retrieving it later.

## Requirements

- Java 17

## Running

```bash
./mvnw spring-boot:run
```

The service starts on `http://localhost:8080`.

To run on a different port or change where notes are stored:

```bash
./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="--server.port=9090 --notes.storage-dir=/tmp/my-notes"
```

## Endpoints

A note has two fields:

```json
{ "title": "grocery-list", "content": "milk, eggs, bread" }
```

The **title is also the note's identifier** — it is how you retrieve the note
later.

### Write a note — `POST /notes`

Writes a note.

```bash
curl -i -X POST http://localhost:8080/notes \
  -H 'Content-Type: application/json' \
  -d '{"title":"grocery-list","content":"milk, eggs, bread"}'
```

| Response | Meaning |
|----------|---------|
| `201 Created` | Note was written. |
| `400 Bad Request` | Title was blank or invalid (see [Title rules](#title-rules)). |

### Read a note — `GET /notes/{title}`

```bash
curl -i http://localhost:8080/notes/grocery-list
```

```json
{ "title": "grocery-list", "content": "milk, eggs, bread" }
```

| Response | Meaning |
|----------|---------|
| `200 OK` | Returns the note as JSON. |
| `404 Not Found` | No note exists with that title. |
| `400 Bad Request` | Title was invalid (see [Title rules](#title-rules)). |

## Storage

- Each note is stored as a single plain-text file named `{title}.txt`.
- Files live in the directory configured by `notes.storage-dir`
  (default: `./data/notes`). The directory is created on startup if missing.
- The file contents are the note's `content`; the file name carries the title.

## Title rules

Because the title becomes a file name, it is validated to prevent escaping the
storage directory (path traversal). A title:

- must not be blank,
- may only contain letters, digits, `.`, `-`, and `_`,
- may not contain `..`.

Invalid titles return `400 Bad Request`. This validation is a deliberate point
of interest for discussion.

## Testing

```bash
./mvnw test
```

The suite covers the filesystem repository (round-trip, overwrite, and title
validation against a temp directory) and the controller (create, retrieve,
`404`, and `400`).
