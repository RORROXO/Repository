# AI prompt log: Stage 2

Bitácora de prompts del Stage 2 (sección 6.2 y Apéndice A de `refactor.pdf`). Cada fila copia el prompt de forma literal. Los saltos de línea se muestran como `<br>`, y los caracteres `|`, `<`, `>` y `&` van escapados como entidades HTML para que la tabla Markdown se vea bien.

| Number | Model | Exact prompt text | Files changed |
|--------|-------|-------------------|---------------|
| 1 | claude-opus-5-5 | Hola, empecemos con el stage 2 | `docs/prompts-stage-2.md` (lectura del código y plan del Stage 2) |
| 2 | claude-opus-5-5 | confirmo, te autorizo en aplicar el orden propuesto y con ese punto. Recuerda que cada stage listo debo revisarlo y luego te autorizo en aplicar el commit. | `src/main/java/cl/ucn/disc/arqsist/library/service/LoanService.java`, `src/main/java/cl/ucn/disc/arqsist/library/App.java`, `docs/prompts-stage-2.md` |
| 3 | claude-opus-5-5 | Revisé LoanService: checkout y returnLoan usan transacción y el clock. Autorizo el commit. Sigue con el Change 9 (sección 2.10): ReservationService recibe MemberDao, LoanDao, BookService y Clock. reserve() valida con NotFoundException. fulfill() distingue entre reserva inexistente (404) y ya cumplida (409), y ejecuta la actualización, bookService.borrow() y la creación del préstamo en una sola transacción. Elimina el campo BookDao. | `src/main/java/cl/ucn/disc/arqsist/library/service/ReservationService.java`, `src/main/java/cl/ucn/disc/arqsist/library/App.java`, `src/test/java/cl/ucn/disc/arqsist/library/DueDateDuplicationTest.java`, `docs/prompts-stage-2.md` |
