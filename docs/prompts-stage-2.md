# AI prompt log: Stage 2

Bitácora de prompts del Stage 2 (sección 6.2 y Apéndice A de `refactor.pdf`). Cada fila copia el prompt de forma literal. Los saltos de línea se muestran como `<br>`, y los caracteres `|`, `<`, `>` y `&` van escapados como entidades HTML para que la tabla Markdown se vea bien.

| Number | Model | Exact prompt text | Files changed |
|--------|-------|-------------------|---------------|
| 1 | claude-opus-5-5 | Hola, empecemos con el stage 2 | `docs/prompts-stage-2.md` (lectura del código y plan del Stage 2) |
| 2 | claude-opus-5-5 | confirmo, te autorizo en aplicar el orden propuesto y con ese punto. Recuerda que cada stage listo debo revisarlo y luego te autorizo en aplicar el commit. | `src/main/java/cl/ucn/disc/arqsist/library/service/LoanService.java`, `src/main/java/cl/ucn/disc/arqsist/library/App.java`, `docs/prompts-stage-2.md` |
| 3 | claude-opus-5-5 | Revisé LoanService: checkout y returnLoan usan transacción y el clock. Autorizo el commit. Sigue con el Change 9 (sección 2.10): ReservationService recibe MemberDao, LoanDao, BookService y Clock. reserve() valida con NotFoundException. fulfill() distingue entre reserva inexistente (404) y ya cumplida (409), y ejecuta la actualización, bookService.borrow() y la creación del préstamo en una sola transacción. Elimina el campo BookDao. | `src/main/java/cl/ucn/disc/arqsist/library/service/ReservationService.java`, `src/main/java/cl/ucn/disc/arqsist/library/App.java`, `src/test/java/cl/ucn/disc/arqsist/library/DueDateDuplicationTest.java`, `docs/prompts-stage-2.md` |
| 4 | claude-opus-5-5 | ReservationService cumple con la sección 2.10. Haz el commit. Continúa con el Change 12 (sección 2.13): BookController sin BookDao y con GET /books llamando a service.listAll(). LoanController sin MemberService y con POST /loans llamando a loanService.checkout(). ReservationController sin Objects.requireNonNull ni el import de java.util.Objects. | `src/main/java/cl/ucn/disc/arqsist/library/controller/BookController.java`, `src/main/java/cl/ucn/disc/arqsist/library/controller/LoanController.java`, `src/main/java/cl/ucn/disc/arqsist/library/controller/ReservationController.java`, `src/main/java/cl/ucn/disc/arqsist/library/App.java`, `docs/prompts-stage-2.md` |
| 5 | claude-opus-5-5 | Los controllers ya pasan por los servicios. Autorizo el commit. Ahora el Change 7 (sección 2.8): MemberService queda solo con MemberDao. Elimina checkout(), los campos BookDao y LoanDao, y throws SQLException de register() y findAll(). | `src/main/java/cl/ucn/disc/arqsist/library/service/MemberService.java`, `src/main/java/cl/ucn/disc/arqsist/library/App.java`, `src/test/java/cl/ucn/disc/arqsist/library/DueDateDuplicationTest.java`, `src/test/java/cl/ucn/disc/arqsist/library/TransactionBugTest.java`, `docs/prompts-stage-2.md` |

## Notas

### Nota 1: el Change 7 se aplicó junto con la actualización de los dos tests (Change 15, primera parte)

El plan acordado en la fila 1 aplicaba primero la actualización de `DueDateDuplicationTest` y `TransactionBugTest` (Paso 4) y después el Change 7 (Paso 5). El prompt 5 pidió el Change 7 directamente. Al eliminar `MemberService.checkout()`, los dos tests dejaban de compilar, así que se actualizaron en el mismo paso, como indica la sección 2.16: usan `LoanService` con un `Clock` fijo, y `TransactionBugTest` espera `NotFoundException` y ya no importa `@Disabled`.
