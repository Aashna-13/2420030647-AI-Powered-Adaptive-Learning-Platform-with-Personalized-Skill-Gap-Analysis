package com.skillbridge.config;

import com.skillbridge.model.Career;
import com.skillbridge.model.CareerQuestion;
import com.skillbridge.model.QuestionDifficulty;
import com.skillbridge.repository.CareerQuestionRepository;
import com.skillbridge.repository.CareerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(2)
public class CareerDataInitializer implements CommandLineRunner {

    private final CareerRepository careerRepository;
    private final CareerQuestionRepository questionRepository;

    public CareerDataInitializer(CareerRepository careerRepository, CareerQuestionRepository questionRepository) {
        this.careerRepository = careerRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedCareers();
        seedQuestions();
    }

    private void seedCareers() {
        if (careerRepository.count() > 0) {
            return;
        }

        List<Career> careers = List.of(
                new Career(
                        "java-developer",
                        "Java Developer",
                        "Build enterprise-scale backend services, distributed systems, and resilient microservices using Java 17+, OOP principles, Spring Boot, and SQL databases.",
                        80,
                        List.of("Java Fundamentals", "OOP", "Collections", "Exception Handling", "SQL", "Spring Boot"),
                        "bi-filetype-java",
                        "primary"
                ),
                new Career(
                        "full-stack-developer",
                        "Full Stack Developer",
                        "Design and implement complete web applications spanning dynamic frontend user interfaces, robust backend REST APIs, relational databases, and modern web architectures.",
                        80,
                        List.of("HTML", "CSS", "JavaScript", "Backend development", "REST APIs", "Database fundamentals"),
                        "bi-layers",
                        "info"
                ),
                new Career(
                        "data-scientist",
                        "Data Scientist",
                        "Transform raw data into predictive intelligence and strategic insights through exploratory analysis, statistical modeling, machine learning, and data visualization.",
                        80,
                        List.of("Python Fundamentals", "Data Analysis & Pandas", "Statistics & Probability", "Machine Learning", "Data Visualization", "SQL"),
                        "bi-database-gear",
                        "success"
                ),
                new Career(
                        "cloud-engineer",
                        "Cloud Engineer",
                        "Architect, deploy, and maintain highly available cloud infrastructure, containerized workloads, Linux environments, and secure virtual networking.",
                        80,
                        List.of("Cloud fundamentals", "AWS", "Networking", "Linux", "Docker", "Security fundamentals"),
                        "bi-cloud",
                        "warning"
                ),
                new Career(
                        "aiml-engineer",
                        "AI/ML Engineer",
                        "Develop state-of-the-art machine learning models, deep learning architectures, and scalable MLOps deployment pipelines for production inference.",
                        80,
                        List.of("Python for AI", "Mathematics & Linear Algebra", "Deep Learning & Neural Networks", "Model Evaluation", "NLP & Computer Vision", "MLOps"),
                        "bi-robot",
                        "danger"
                ),
                new Career(
                        "cybersecurity-analyst",
                        "Cybersecurity Analyst",
                        "Safeguard enterprise networks and information assets by analyzing threat vectors, implementing cryptographic controls, assessing vulnerabilities, and leading incident response.",
                        80,
                        List.of("Network Security", "Threat Analysis", "Cryptography Basics", "Vulnerability Assessment", "Security Operations", "Incident Response"),
                        "bi-shield-check",
                        "secondary"
                )
        );

        careerRepository.saveAll(careers);
    }

    private void seedQuestions() {
        if (questionRepository.count() > 0) {
            return;
        }

        List<CareerQuestion> questions = new ArrayList<>();
        addJavaDeveloperQuestions(questions);
        addFullStackQuestions(questions);
        addDataScientistQuestions(questions);
        addCloudEngineerQuestions(questions);
        addAimLEngineerQuestions(questions);
        addCybersecurityQuestions(questions);

        questionRepository.saveAll(questions);
    }

    // -------------------------------------------------------------
    // 1. JAVA DEVELOPER (30 Questions: 6 Skills x 5 Questions)
    // -------------------------------------------------------------
    private void addJavaDeveloperQuestions(List<CareerQuestion> q) {
        String c = "java-developer";

        // Skill: Java Fundamentals
        q.add(new CareerQuestion(c, "Java Fundamentals", "Which Java keyword is used to declare a variable that cannot be reassigned?",
                "final", "const", "static", "volatile", 0, QuestionDifficulty.BEGINNER, "The final keyword prevents reassignment after initialization."));
        q.add(new CareerQuestion(c, "Java Fundamentals", "What is the default value of an uninitialized boolean instance variable in Java?",
                "true", "false", "null", "0", 1, QuestionDifficulty.BEGINNER, "Boolean instance variables default to false in Java."));
        q.add(new CareerQuestion(c, "Java Fundamentals", "What is the key difference between String, StringBuilder, and StringBuffer?",
                "String is mutable; StringBuffer and StringBuilder are immutable",
                "StringBuilder is thread-safe, while StringBuffer is not",
                "String is immutable; StringBuffer is thread-safe and synchronized; StringBuilder is non-synchronized and faster",
                "All three classes are completely interchangeable",
                2, QuestionDifficulty.INTERMEDIATE, "String is immutable. StringBuffer is synchronized and thread-safe. StringBuilder is asynchronous and faster."));
        q.add(new CareerQuestion(c, "Java Fundamentals", "Which memory area in the JVM stores method metadata and loaded class definitions in Java 8+?",
                "PermGen", "Metaspace", "Eden Space", "Java Stack", 1, QuestionDifficulty.INTERMEDIATE, "Java 8 replaced PermGen with native-memory Metaspace."));
        q.add(new CareerQuestion(c, "Java Fundamentals", "How do Virtual Threads (Project Loom, Java 21) differ from conventional platform threads?",
                "Virtual threads require dedicated OS-level kernel thread stacks",
                "Virtual threads are lightweight user-mode threads managed by the JVM rather than 1:1 OS threads",
                "Virtual threads can only execute CPU-bound cryptographic calculations",
                "Virtual threads cannot perform blocking I/O operations",
                1, QuestionDifficulty.ADVANCED, "Virtual threads decouple Java threads from 1:1 kernel threads, enabling high-throughput I/O scaling."));

        // Skill: OOP
        q.add(new CareerQuestion(c, "OOP", "Which OOP principle hides internal object state and requires interaction through public methods?",
                "Inheritance", "Polymorphism", "Encapsulation", "Abstraction", 2, QuestionDifficulty.BEGINNER, "Encapsulation bundles data and hides implementation details."));
        q.add(new CareerQuestion(c, "OOP", "Can a Java class implement multiple interfaces?",
                "No, Java only permits single interface implementation",
                "Yes, Java allows a class to implement multiple interfaces",
                "Only if all interfaces have identical method signatures",
                "Only abstract classes can implement multiple interfaces",
                1, QuestionDifficulty.BEGINNER, "Java supports multiple inheritance of type through interfaces."));
        q.add(new CareerQuestion(c, "OOP", "What is the difference between method overloading and method overriding?",
                "Overloading happens at runtime; overriding happens at compile time",
                "Overloading occurs within the same class with different parameters; overriding replaces a superclass method with the same signature in a subclass",
                "Overriding requires static methods, while overloading requires final methods",
                "There is no difference; they are synonymous",
                1, QuestionDifficulty.INTERMEDIATE, "Overloading is compile-time polymorphism; overriding is runtime dynamic dispatch."));
        q.add(new CareerQuestion(c, "OOP", "What is the primary purpose of the 'Liskov Substitution Principle' (LSP) in SOLID design?",
                "Classes should be open for extension but closed for modification",
                "Subtypes must be substitutable for their base types without altering program correctness",
                "Clients should not be forced to depend on interfaces they do not use",
                "High-level modules should not depend on low-level modules directly",
                1, QuestionDifficulty.INTERMEDIATE, "LSP guarantees that derived classes honor the contracts and invariants of base classes."));
        q.add(new CareerQuestion(c, "OOP", "Why is composition generally favored over class inheritance for complex domain models?",
                "Inheritance breaks encapsulation by coupling subclasses to superclass internal implementations (fragile base class)",
                "Composition increases memory consumption dramatically",
                "Inheritance prevents the use of interfaces",
                "Composition requires all methods to be static",
                0, QuestionDifficulty.ADVANCED, "Composition offers loose coupling and runtime flexibility without the fragile base class problem."));

        // Skill: Collections
        q.add(new CareerQuestion(c, "Collections", "Which collection type does NOT allow duplicate elements?",
                "List", "Set", "Queue", "ArrayList", 1, QuestionDifficulty.BEGINNER, "Set implementations model mathematical sets and disallow duplicates."));
        q.add(new CareerQuestion(c, "Collections", "What is the underlying data structure of an ArrayList?",
                "Doubly linked list", "Dynamically resizing array", "Balanced Red-Black tree", "Hash table with buckets", 1, QuestionDifficulty.BEGINNER, "ArrayList wraps a dynamically growing primitive Object array."));
        q.add(new CareerQuestion(c, "Collections", "What is the average time complexity for get() and put() in a java.util.HashMap with a good hash function?",
                "O(n)", "O(log n)", "O(1)", "O(n log n)", 2, QuestionDifficulty.INTERMEDIATE, "HashMap provides O(1) expected time for lookup and insertion."));
        q.add(new CareerQuestion(c, "Collections", "How does ConcurrentHashMap achieve thread safety without synchronizing the entire map?",
                "It locks the entire instance on every write operation",
                "It uses lock-free CAS operations and synchronizes only individual bucket head nodes (segment-level concurrency)",
                "It creates an immutable copy of the array on every insertion",
                "It delegates all requests to a single-threaded background queue",
                1, QuestionDifficulty.INTERMEDIATE, "ConcurrentHashMap uses CAS for initial bin insertion and bucket-level synchronized blocks."));
        q.add(new CareerQuestion(c, "Collections", "What happens in a Java 8+ HashMap when hash collisions in a single bucket exceed the TREEIFY_THRESHOLD (8)?",
                "The table throws an OutOfMemoryError immediately",
                "The colliding linked list is converted into a Red-Black tree node structure (TreeNode) to improve worst-case search to O(log n)",
                "The entire map is cleared and re-hashed with double capacity",
                "Colliding elements are discarded silently",
                1, QuestionDifficulty.ADVANCED, "Treeification prevents denial-of-service hash attacks by bounding worst-case search to O(log n)."));

        // Skill: Exception Handling
        q.add(new CareerQuestion(c, "Exception Handling", "Which keyword is used to explicitly throw an exception object in Java?",
                "throw", "throws", "try", "catch", 0, QuestionDifficulty.BEGINNER, "The throw keyword instantiates and raises an exception."));
        q.add(new CareerQuestion(c, "Exception Handling", "Which block is guaranteed to execute regardless of whether an exception is thrown in a try-catch construct?",
                "catch", "finally", "throws", "when", 1, QuestionDifficulty.BEGINNER, "finally executes cleanup logic regardless of normal completion or exceptions."));
        q.add(new CareerQuestion(c, "Exception Handling", "What distinguishes checked exceptions from unchecked exceptions in Java?",
                "Checked exceptions inherit from RuntimeException and do not require declaration",
                "Checked exceptions extend Exception (excluding RuntimeException) and must be handled or declared in the method signature with throws",
                "Unchecked exceptions can never be caught in a catch block",
                "Checked exceptions only occur in multi-threaded code",
                1, QuestionDifficulty.INTERMEDIATE, "Checked exceptions are enforced by the compiler at compile time."));
        q.add(new CareerQuestion(c, "Exception Handling", "How does Java 7+ 'try-with-resources' manage resource cleanup?",
                "It invokes System.gc() after every statement",
                "It automatically closes resources implementing java.lang.AutoCloseable in reverse order of declaration",
                "It spawns a daemon thread to monitor open file descriptors",
                "It requires manual invocation of resource.close() inside finally",
                1, QuestionDifficulty.INTERMEDIATE, "try-with-resources guarantees deterministic cleanup for AutoCloseable resources."));
        q.add(new CareerQuestion(c, "Exception Handling", "What is an 'exception suppression' scenario in Java try-with-resources?",
                "When an exception in the try block prevents the finally block from running",
                "When an exception occurs while closing an AutoCloseable and is attached to the primary exception via Throwable.addSuppressed()",
                "When unchecked exceptions are converted into checked exceptions",
                "When the compiler ignores unhandled exceptions in lambda expressions",
                1, QuestionDifficulty.ADVANCED, "Suppressed exceptions preserve the original cause while surfacing secondary closing errors."));

        // Skill: SQL
        q.add(new CareerQuestion(c, "SQL", "Which SQL clause is used to filter records returned by a SELECT query?",
                "GROUP BY", "WHERE", "ORDER BY", "HAVING", 1, QuestionDifficulty.BEGINNER, "WHERE filters rows before any groupings are formed."));
        q.add(new CareerQuestion(c, "SQL", "Which SQL command permanently removes a table along with its schema and structure?",
                "DELETE TABLE", "TRUNCATE", "DROP TABLE", "REMOVE TABLE", 2, QuestionDifficulty.BEGINNER, "DROP TABLE removes both table definition and rows from the database."));
        q.add(new CareerQuestion(c, "SQL", "What is the difference between WHERE and HAVING in SQL?",
                "WHERE filters aggregated groups; HAVING filters individual rows before grouping",
                "WHERE filters individual rows before grouping; HAVING filters aggregated groups created by GROUP BY",
                "WHERE is only used with JOIN; HAVING is only used with subqueries",
                "There is no difference; both clauses are interchangeable",
                1, QuestionDifficulty.INTERMEDIATE, "WHERE filters rows prior to aggregation; HAVING filters aggregated metric results."));
        q.add(new CareerQuestion(c, "SQL", "What ACID property ensures that committed transactions remain durable even in the event of a system crash or power outage?",
                "Atomicity", "Consistency", "Isolation", "Durability", 3, QuestionDifficulty.INTERMEDIATE, "Durability ensures committed data is written to non-volatile storage (WAL/redo logs)."));
        q.add(new CareerQuestion(c, "SQL", "In MySQL InnoDB, what is the primary benefit of a 'covering index'?",
                "It encrypts table data at rest",
                "The index contains all required query columns, allowing the engine to satisfy the query entirely from index leaf pages without a secondary row lookup",
                "It automatically partitions tables across multiple cluster nodes",
                "It enforces referential foreign key integrity automatically",
                1, QuestionDifficulty.ADVANCED, "Covering indexes eliminate costly table data page seeks (avoiding bookmark lookup)."));

        // Skill: Spring Boot
        q.add(new CareerQuestion(c, "Spring Boot", "Which annotation marks a class as a Spring-managed REST controller that serializes responses to JSON?",
                "@Controller", "@RestController", "@Service", "@Component", 1, QuestionDifficulty.BEGINNER, "@RestController combines @Controller and @ResponseBody."));
        q.add(new CareerQuestion(c, "Spring Boot", "What is the default embedded web servlet container in Spring Boot Starter Web?",
                "Jetty", "Undertow", "Tomcat", "GlassFish", 2, QuestionDifficulty.BEGINNER, "Apache Tomcat is the default embedded servlet container."));
        q.add(new CareerQuestion(c, "Spring Boot", "What is the difference between @Autowired field injection and constructor injection?",
                "Field injection is easier to mock in unit tests and makes dependencies explicit",
                "Constructor injection ensures immutability with final fields, explicit dependency contracts, and easier unit testing without a Spring context",
                "Field injection is required for Spring Data JPA repositories",
                "There is no functional difference between them",
                1, QuestionDifficulty.INTERMEDIATE, "Constructor injection promotes immutability and simplifies isolated unit testing."));
        q.add(new CareerQuestion(c, "Spring Boot", "How does Spring Boot auto-configuration determine which beans to instantiate?",
                "It scans all classes on the internet dynamically",
                "It evaluates conditional annotations such as @ConditionalOnClass, @ConditionalOnMissingBean, and property configs",
                "It requires every bean to be manually declared in an XML file",
                "It generates new bytecode on every HTTP request",
                1, QuestionDifficulty.INTERMEDIATE, "Spring Boot uses @Conditional annotations to configure beans only when matching dependencies and properties exist."));
        q.add(new CareerQuestion(c, "Spring Boot", "How does Spring transaction management (@Transactional) work under the hood?",
                "It compiles custom JVM instructions into the operating system kernel",
                "It uses Spring AOP proxies (JDK dynamic proxy or CGLIB) to intercept method calls and manage transaction boundaries on a PlatformTransactionManager",
                "It opens a permanent database connection for every user session",
                "It executes all methods on a dedicated database thread",
                1, QuestionDifficulty.ADVANCED, "Spring AOP proxies intercept calls to begin, commit, or rollback transactions via PlatformTransactionManager."));
    }

    // -------------------------------------------------------------
    // 2. FULL STACK DEVELOPER (30 Questions: 6 Skills x 5 Questions)
    // -------------------------------------------------------------
    private void addFullStackQuestions(List<CareerQuestion> q) {
        String c = "full-stack-developer";

        // Skill: HTML
        q.add(new CareerQuestion(c, "HTML", "Which semantic HTML5 tag represents the main introductory header or branding for a webpage?",
                "<section>", "<header>", "<nav>", "<article>", 1, QuestionDifficulty.BEGINNER, "<header> represents introductory content or navigational aids."));
        q.add(new CareerQuestion(c, "HTML", "Which HTML attribute specifies alternate text for screen readers and search engines when an image cannot load?",
                "title", "src", "alt", "href", 2, QuestionDifficulty.BEGINNER, "The alt attribute provides alternate text for accessibility and SEO."));
        q.add(new CareerQuestion(c, "HTML", "What is the difference between <script async> and <script defer>?",
                "async executes during HTML parsing as soon as downloaded; defer downloads in parallel and executes in document order after parsing completes",
                "defer blocks DOM construction immediately, whereas async does not",
                "async is only for stylesheets; defer is for JavaScript",
                "Both attributes behave identically in modern browsers",
                0, QuestionDifficulty.INTERMEDIATE, "async executes immediately upon download; defer waits until the document is parsed."));
        q.add(new CareerQuestion(c, "HTML", "Why are semantic elements like <main>, <article>, and <nav> preferred over generic <div> tags?",
                "They render faster in web graphics accelerators",
                "They improve accessibility for assistive technology and provide structural meaning for search engine crawlers",
                "They prevent JavaScript errors during execution",
                "They allow inline CSS styling without classes",
                1, QuestionDifficulty.INTERMEDIATE, "Semantic HTML enables screen readers and crawlers to navigate document outlines accurately."));
        q.add(new CareerQuestion(c, "HTML", "What is the purpose of the 'Content Security Policy' (CSP) HTTP header or meta tag?",
                "To optimize JPEG image rendering performance",
                "To restrict approved origins from which scripts, styles, and assets can be loaded, mitigating XSS attacks",
                "To enable automatic responsive breakpoints",
                "To gzip HTML payloads before transmission",
                1, QuestionDifficulty.ADVANCED, "CSP defends against Cross-Site Scripting (XSS) and data injection attacks."));

        // Skill: CSS
        q.add(new CareerQuestion(c, "CSS", "Which CSS property defines the space between an element's border and its inner content?",
                "margin", "padding", "outline", "border-spacing", 1, QuestionDifficulty.BEGINNER, "padding sets inner spacing inside an element's border."));
        q.add(new CareerQuestion(c, "CSS", "What is the CSS display value used to create a 1-dimensional flexbox layout?",
                "display: block", "display: flex", "display: grid", "display: inline", 1, QuestionDifficulty.BEGINNER, "display: flex establishes a flex formatting context."));
        q.add(new CareerQuestion(c, "CSS", "What does 'box-sizing: border-box' accomplish in modern web design?",
                "It adds a 3D drop shadow to all borders",
                "It includes padding and border within the element's specified width and height",
                "It resets all margins to zero automatically",
                "It forces elements to display as inline-blocks",
                1, QuestionDifficulty.INTERMEDIATE, "border-box simplifies layout math by factoring padding and borders into total width."));
        q.add(new CareerQuestion(c, "CSS", "In CSS specificity calculations, which selector has the highest precedence?",
                "Element type selector (e.g. p)",
                "Class selector (e.g. .btn)",
                "ID selector (e.g. #main-nav)",
                "Universal selector (*)",
                2, QuestionDifficulty.INTERMEDIATE, "ID selectors have higher specificity than classes, attributes, and elements."));
        q.add(new CareerQuestion(c, "CSS", "What is the primary architectural difference between CSS Flexbox and CSS Grid?",
                "Flexbox is designed for 1-dimensional layouts (row or column); Grid is designed for 2-dimensional layouts (rows and columns simultaneously)",
                "Flexbox only supports text elements; Grid only supports images",
                "Grid cannot be used with media queries",
                "Flexbox requires JavaScript polyfills in modern browsers",
                0, QuestionDifficulty.ADVANCED, "Flexbox handles 1D content-driven flow; Grid manages 2D layout systems."));

        // Skill: JavaScript
        q.add(new CareerQuestion(c, "JavaScript", "Which keyword is used to declare block-scoped variables that cannot be reassigned?",
                "var", "let", "const", "def", 2, QuestionDifficulty.BEGINNER, "const declares block-scoped, non-reassignable bindings."));
        q.add(new CareerQuestion(c, "JavaScript", "What is the result of typeof null in JavaScript?",
                "'null'", "'undefined'", "'object'", "'boolean'", 2, QuestionDifficulty.BEGINNER, "typeof null returns 'object' due to a historical legacy bug in JS."));
        q.add(new CareerQuestion(c, "JavaScript", "What is a closure in JavaScript?",
                "A function bundled together with references to its surrounding lexical environment",
                "A syntax error caused by unclosed curly braces",
                "A built-in method that shuts down the browser tab",
                "A design pattern used exclusively for database connections",
                0, QuestionDifficulty.INTERMEDIATE, "Closures retain access to outer variables even after the outer function has finished executing."));
        q.add(new CareerQuestion(c, "JavaScript", "What is the difference between == and === in JavaScript?",
                "== checks both value and type without conversion; === converts types automatically",
                "== performs type coercion before comparison; === checks strict equality of value and type without coercion",
                "=== is only valid for numbers",
                "There is no difference; they are aliases",
                1, QuestionDifficulty.INTERMEDIATE, "=== is strict equality; == performs implicit type coercion."));
        q.add(new CareerQuestion(c, "JavaScript", "How does the JavaScript Event Loop handle microtasks vs macrotasks?",
                "Macrotasks always execute before any microtasks in every cycle",
                "The microtask queue (Promise callbacks, queueMicrotask) is completely drained after each macrotask before the next macrotask runs",
                "Microtasks and macrotasks run concurrently on multi-threaded worker pools",
                "Microtasks are executed only when the user scrolls the page",
                1, QuestionDifficulty.ADVANCED, "All pending microtasks are flushed before the browser proceeds to rendering or the next macrotask."));

        // Skill: Backend development
        q.add(new CareerQuestion(c, "Backend development", "What is the primary role of middleware in a web server pipeline?",
                "To compile CSS into binary format",
                "To intercept, inspect, modify, or authenticate HTTP requests and responses before reaching handlers",
                "To manage browser cookie popups on client screens",
                "To optimize GPU shader performance",
                1, QuestionDifficulty.BEGINNER, "Middleware inspects and processes requests before routing to endpoint controllers."));
        q.add(new CareerQuestion(c, "Backend development", "Which HTTP status code represents '404 Not Found'?",
                "200", "401", "404", "500", 2, QuestionDifficulty.BEGINNER, "404 indicates the requested server resource was not found."));
        q.add(new CareerQuestion(c, "Backend development", "What is the difference between session-based authentication and JWT token-based authentication?",
                "Sessions store state on the server (e.g. in Redis/DB); JWTs are stateless and self-contained with cryptographic signatures verified by the server",
                "JWTs can only be transmitted over HTTP, not HTTPS",
                "Session cookies cannot be used in web browsers",
                "JWTs encrypt all user data so it cannot be read anywhere",
                0, QuestionDifficulty.INTERMEDIATE, "JWTs carry stateless claims verified by signatures; session auth maintains server-side session stores."));
        q.add(new CareerQuestion(c, "Backend development", "What is the primary purpose of database connection pooling in backend services?",
                "To download database backups to local memory",
                "To reuse pre-established database connections, avoiding the expensive overhead of creating new TCP/TLS connections on every request",
                "To prevent database queries from using SQL syntax",
                "To automatically shard tables across different cloud providers",
                1, QuestionDifficulty.INTERMEDIATE, "Connection pools maintain open connections to handle incoming traffic with minimal latency."));
        q.add(new CareerQuestion(c, "Backend development", "How does an API Gateway help protect backend microservices?",
                "By writing all frontend HTML templates automatically",
                "By providing centralized rate limiting, authentication, SSL termination, and request routing before traffic reaches internal services",
                "By eliminating the need for relational databases",
                "By running client-side unit tests in browser memory",
                1, QuestionDifficulty.ADVANCED, "API Gateways centralize cross-cutting concerns like security, rate limiting, and routing."));

        // Skill: REST APIs
        q.add(new CareerQuestion(c, "REST APIs", "Which HTTP method is conventionally used to update an entire existing resource?",
                "GET", "POST", "PUT", "DELETE", 2, QuestionDifficulty.BEGINNER, "PUT performs full replacement or creation of a resource."));
        q.add(new CareerQuestion(c, "REST APIs", "What does it mean for an HTTP method to be 'idempotent'?",
                "The method executes faster than non-idempotent methods",
                "Making multiple identical requests produces the exact same server state result as a single request",
                "The method can never be cached by proxy servers",
                "The method only accepts XML payloads",
                1, QuestionDifficulty.BEGINNER, "Idempotence guarantees duplicate requests have the same intended effect as one request."));
        q.add(new CareerQuestion(c, "REST APIs", "Which HTTP status code should be returned after successfully creating a new resource via POST?",
                "200 OK", "201 Created", "204 No Content", "304 Not Modified", 1, QuestionDifficulty.INTERMEDIATE, "201 Created signals successful resource creation."));
        q.add(new CareerQuestion(c, "REST APIs", "What is the purpose of HATEOAS in mature RESTful API design?",
                "To compress JSON payloads with GZIP",
                "To provide hypermedia links in responses guiding clients on available state transitions and actions",
                "To enforce XML formatting over JSON",
                "To encrypt all database tables automatically",
                1, QuestionDifficulty.INTERMEDIATE, "HATEOAS provides dynamic hypermedia links for API discoverability."));
        q.add(new CareerQuestion(c, "REST APIs", "How does HTTP conditional request handling with ETag and If-None-Match optimize API traffic?",
                "It enables the client to verify cache validity, returning 304 Not Modified without resending unchanged body data",
                "It converts all REST endpoints into WebSocket connections",
                "It encrypts payload fields using symmetric block ciphers",
                "It removes the need for JSON parsing on mobile devices",
                0, QuestionDifficulty.ADVANCED, "ETags allow efficient cache validation and bandwidth savings via 304 responses."));

        // Skill: Database fundamentals
        q.add(new CareerQuestion(c, "Database fundamentals", "What is a Primary Key in relational database modeling?",
                "A column that allows duplicate and null values",
                "A unique identifier column that uniquely identifies each row in a table and cannot be null",
                "An index used exclusively for text search",
                "A temporary key discarded after query execution",
                1, QuestionDifficulty.BEGINNER, "Primary keys uniquely identify table records and enforce uniqueness without nulls."));
        q.add(new CareerQuestion(c, "Database fundamentals", "What type of relationship exists between an Author and their Books?",
                "One-to-One", "One-to-Many", "Many-to-Many", "Zero-to-Zero", 1, QuestionDifficulty.BEGINNER, "One author typically writes many books (One-to-Many)."));
        q.add(new CareerQuestion(c, "Database fundamentals", "What is the primary goal of database normalization (1NF, 2NF, 3NF)?",
                "To maximize duplicate columns for faster reading",
                "To minimize data redundancy and eliminate anomalies in insert, update, and delete operations",
                "To convert SQL tables into NoSQL document collections",
                "To encrypt table columns on disk",
                1, QuestionDifficulty.INTERMEDIATE, "Normalization eliminates update anomalies and redundant data storage."));
        q.add(new CareerQuestion(c, "Database fundamentals", "What is a Foreign Key constraint?",
                "A key generated by external third-party microservices",
                "A referential constraint ensuring a column's values correspond to valid primary keys in another table",
                "A mechanism for encrypting database passwords",
                "A temporary table used during database migrations",
                1, QuestionDifficulty.INTERMEDIATE, "Foreign keys enforce referential integrity across related tables."));
        q.add(new CareerQuestion(c, "Database fundamentals", "What is the difference between an Optimistic Lock and a Pessimistic Lock in concurrent transactions?",
                "Optimistic locking holds database-level row locks until commit; pessimistic locking does not use transactions",
                "Optimistic locking checks version numbers on update without locking rows upfront; pessimistic locking locks rows immediately with SELECT FOR UPDATE",
                "Optimistic locking is only for NoSQL databases",
                "Pessimistic locking always allows dirty reads",
                1, QuestionDifficulty.ADVANCED, "Optimistic locking detects concurrent modifications via versioning; pessimistic locking acquires explicit exclusive locks."));
    }

    // -------------------------------------------------------------
    // 3. DATA SCIENTIST (30 Questions: 6 Skills x 5 Questions)
    // -------------------------------------------------------------
    private void addDataScientistQuestions(List<CareerQuestion> q) {
        String c = "data-scientist";

        // Skill: Python Fundamentals
        q.add(new CareerQuestion(c, "Python Fundamentals", "Which Python data structure is mutable and ordered?",
                "tuple", "list", "frozenset", "string", 1, QuestionDifficulty.BEGINNER, "Lists are mutable ordered sequences in Python."));
        q.add(new CareerQuestion(c, "Python Fundamentals", "Which keyword is used to define a function in Python?",
                "func", "function", "def", "lambda", 2, QuestionDifficulty.BEGINNER, "def declares named functions in Python."));
        q.add(new CareerQuestion(c, "Python Fundamentals", "What is a Python generator and which keyword creates one?",
                "A class that inherits from dict; uses return",
                "A function that returns an iterator and yields values one at a time using the yield keyword",
                "A compiler plugin that translates Python to C",
                "A module for generating random passwords",
                1, QuestionDifficulty.INTERMEDIATE, "Generators produce items lazily on demand using the yield statement."));
        q.add(new CareerQuestion(c, "Python Fundamentals", "What does the Python GIL (Global Interpreter Lock) enforce?",
                "It prevents Python scripts from importing external libraries",
                "It allows only one thread to execute Python bytecode at a time within a single CPython process",
                "It encrypts variables stored in memory",
                "It limits functions to a maximum of 10 parameters",
                1, QuestionDifficulty.INTERMEDIATE, "The GIL synchronizes thread execution in CPython to protect memory management."));
        q.add(new CareerQuestion(c, "Python Fundamentals", "What is the time complexity of dictionary key lookup in Python on average?",
                "O(n)", "O(log n)", "O(1)", "O(n^2)", 2, QuestionDifficulty.ADVANCED, "Python dicts use open-addressing hash tables providing O(1) average lookup."));

        // Skill: Data Analysis & Pandas
        q.add(new CareerQuestion(c, "Data Analysis & Pandas", "What is the primary 2-dimensional tabular data structure in pandas?",
                "Series", "DataFrame", "Panel", "Tensor", 1, QuestionDifficulty.BEGINNER, "DataFrame is the foundational 2D tabular structure in pandas."));
        q.add(new CareerQuestion(c, "Data Analysis & Pandas", "Which method removes missing (NaN) values from a pandas DataFrame?",
                "df.drop_na()", "df.dropna()", "df.remove_null()", "df.clean()", 1, QuestionDifficulty.BEGINNER, "df.dropna() drops rows or columns containing null values."));
        q.add(new CareerQuestion(c, "Data Analysis & Pandas", "What is the difference between .loc and .iloc in pandas?",
                ".loc uses integer indices; .iloc uses label-based indexing",
                ".loc uses label-based indexing; .iloc uses zero-based integer positional indexing",
                ".loc is for Series; .iloc is for DataFrames only",
                "They are identical aliases",
                1, QuestionDifficulty.INTERMEDIATE, ".loc indexes by labels/names; .iloc indexes by numeric positional offsets."));
        q.add(new CareerQuestion(c, "Data Analysis & Pandas", "What operation does df.groupby('category').agg({'revenue': 'sum'}) perform?",
                "It sorts the dataframe alphabetically by category",
                "It groups rows by category and calculates the sum of revenue for each group",
                "It removes all revenue entries with duplicate values",
                "It converts categorical strings into one-hot encoded vectors",
                1, QuestionDifficulty.INTERMEDIATE, "groupby aggregates metrics per unique group value."));
        q.add(new CareerQuestion(c, "Data Analysis & Pandas", "Why is vectorized computation in pandas/NumPy faster than iterating through rows with for loops?",
                "Vectorization runs pre-compiled C code over contiguous memory blocks (SIMD), avoiding Python interpreter loop overhead",
                "Vectorization compiles Python scripts directly to JavaScript",
                "Vectorization uses cloud server GPUs automatically",
                "For loops consume twice as much memory in all operating systems",
                0, QuestionDifficulty.ADVANCED, "NumPy/Pandas vectorized operations leverage C routines and SIMD vector registers."));

        // Skill: Statistics & Probability
        q.add(new CareerQuestion(c, "Statistics & Probability", "What metric represents the middle value in a sorted distribution of numbers?",
                "Mean", "Median", "Mode", "Standard Deviation", 1, QuestionDifficulty.BEGINNER, "Median is the midpoint separating higher and lower halves of data."));
        q.add(new CareerQuestion(c, "Statistics & Probability", "What is the sum of probabilities across all possible mutually exclusive outcomes in an event?",
                "0.5", "1.0", "100.0", "Infinity", 1, QuestionDifficulty.BEGINNER, "Total probability across an exhaustive sample space equals 1.0."));
        q.add(new CareerQuestion(c, "Statistics & Probability", "What does the Central Limit Theorem state?",
                "All real-world populations follow a Poisson distribution",
                "The sampling distribution of the sample mean approaches a normal distribution as sample size increases, regardless of population shape",
                "Correlation always proves direct causation",
                "Outliers are always distributed symmetrically around zero",
                1, QuestionDifficulty.INTERMEDIATE, "The Central Limit Theorem guarantees asymptotic normality of sample means."));
        q.add(new CareerQuestion(c, "Statistics & Probability", "What is a p-value in statistical hypothesis testing?",
                "The probability that the alternate hypothesis is completely true",
                "The probability of observing test results at least as extreme as the observed data, assuming the null hypothesis is true",
                "The exact percentage of errors in the dataset",
                "The ratio of variance to the mean",
                1, QuestionDifficulty.INTERMEDIATE, "A p-value quantifies evidence against the null hypothesis under assumption of truth."));
        q.add(new CareerQuestion(c, "Statistics & Probability", "What is the difference between Type I and Type II errors?",
                "Type I is false negative; Type II is false positive",
                "Type I is rejecting a true null hypothesis (false positive); Type II is failing to reject a false null hypothesis (false negative)",
                "Type I occurs in training; Type II occurs in testing",
                "Type I error is always 0.05",
                1, QuestionDifficulty.ADVANCED, "Type I is false positive (alpha); Type II is false negative (beta)."));

        // Skill: Machine Learning
        q.add(new CareerQuestion(c, "Machine Learning", "Which machine learning paradigm uses labeled input-output pairs to train models?",
                "Unsupervised learning", "Supervised learning", "Reinforcement learning", "Self-supervised clustering", 1, QuestionDifficulty.BEGINNER, "Supervised learning learns mappings from labeled ground truth datasets."));
        q.add(new CareerQuestion(c, "Machine Learning", "Which algorithm is commonly used for classification by finding the maximum margin hyperplane?",
                "Linear Regression", "Support Vector Machines (SVM)", "K-Means", "PCA", 1, QuestionDifficulty.BEGINNER, "SVM seeks the optimal hyperplane with maximum geometric margin."));
        q.add(new CareerQuestion(c, "Machine Learning", "What is the bias-variance tradeoff in machine learning?",
                "High bias causes overfitting; high variance causes underfitting",
                "High bias leads to underfitting (oversimplified models); high variance leads to overfitting (capturing dataset noise)",
                "Bias and variance are always inversely equal to learning rate",
                "Variance measures training loss; bias measures testing loss",
                1, QuestionDifficulty.INTERMEDIATE, "Bias is error from erroneous assumptions; variance is sensitivity to fluctuations in training data."));
        q.add(new CareerQuestion(c, "Machine Learning", "How does Random Forest reduce the variance of individual Decision Trees?",
                "By training trees sequentially on residual errors",
                "By bagging (bootstrap aggregating) multiple trees trained on random feature subsets and averaging their predictions",
                "By pruning trees until only one leaf node remains",
                "By applying L1 regularization to all tree branches",
                1, QuestionDifficulty.INTERMEDIATE, "Random Forest averages decorrelated trees trained on bootstrap samples with random features."));
        q.add(new CareerQuestion(c, "Machine Learning", "What is the primary difference between Bagging and Boosting ensemble methods?",
                "Bagging trains models in parallel independently; Boosting trains models sequentially, where each model focuses on errors of previous ones",
                "Boosting always uses linear models; Bagging always uses neural networks",
                "Bagging cannot be used for classification problems",
                "There is no difference; both are synonyms for k-fold cross validation",
                0, QuestionDifficulty.ADVANCED, "Bagging reduces variance via parallel voting; Boosting reduces bias via sequential error correction."));

        // Skill: Data Visualization
        q.add(new CareerQuestion(c, "Data Visualization", "Which Python library is the standard foundation for 2D static plots (plt.plot)?",
                "Scikit-learn", "Matplotlib", "PyTorch", "Flask", 1, QuestionDifficulty.BEGINNER, "Matplotlib is the core plotting library in the Python data ecosystem."));
        q.add(new CareerQuestion(c, "Data Visualization", "Which chart type is best suited to display the distribution and skewness of a single continuous variable?",
                "Pie chart", "Histogram / KDE plot", "Scatter plot", "Network graph", 1, QuestionDifficulty.BEGINNER, "Histograms group values into bins to show distribution shape."));
        q.add(new CareerQuestion(c, "Data Visualization", "What does a Box Plot (Box-and-Whisker) visualize?",
                "Only the maximum and minimum values of a dataset",
                "The median, interquartile range (25th and 75th percentiles), and statistical outliers",
                "A 3D surface mesh of gradient vectors",
                "Linear regression coefficients over time",
                1, QuestionDifficulty.INTERMEDIATE, "Box plots display median, IQR (Q1, Q3), whiskers, and outliers."));
        q.add(new CareerQuestion(c, "Data Visualization", "When is a Heatmap particularly effective in exploratory data analysis?",
                "For visualizing correlation matrices between multiple continuous features",
                "For plotting time-series data with uneven intervals",
                "For displaying geographic road coordinates only",
                "For rendering 3D video animations",
                0, QuestionDifficulty.INTERMEDIATE, "Correlation heatmaps reveal multicollinearity and feature interactions visually."));
        q.add(new CareerQuestion(c, "Data Visualization", "Why should 3D pie charts generally be avoided in statistical communication?",
                "They require expensive WebGL browser engines",
                "Perspective distortion causes angles and slices closer to the viewer to appear misleadingly larger than their true proportions",
                "They cannot display more than two categories",
                "Color blind users can never perceive pie charts",
                1, QuestionDifficulty.ADVANCED, "3D foreshortening distorts area perception, leading to misinterpretation of relative sizes."));

        // Skill: SQL (Data Science context)
        q.add(new CareerQuestion(c, "SQL", "Which SQL function counts the total number of rows matching a criteria?",
                "SUM()", "COUNT()", "AVG()", "TOTAL()", 1, QuestionDifficulty.BEGINNER, "COUNT() aggregates the number of matching records."));
        q.add(new CareerQuestion(c, "SQL", "What is the purpose of the DISTINCT keyword in a SELECT statement?",
                "To return rows sorted in descending order",
                "To eliminate duplicate values from the output query results",
                "To encrypt sensitive database column values",
                "To join tables on matching primary keys",
                1, QuestionDifficulty.BEGINNER, "DISTINCT filters out identical duplicate rows in query results."));
        q.add(new CareerQuestion(c, "SQL", "What is an SQL Window Function and how does it differ from GROUP BY?",
                "It creates a graphical window in the database console",
                "It calculates metrics over a partition of rows while retaining individual row identities, unlike GROUP BY which collapses rows",
                "It is only available in NoSQL document stores",
                "It can only be used with date columns",
                1, QuestionDifficulty.INTERMEDIATE, "Window functions (e.g. ROW_NUMBER, RANK) compute over partitions without collapsing records."));
        q.add(new CareerQuestion(c, "SQL", "How does a Common Table Expression (CTE, WITH clause) improve complex SQL data workflows?",
                "It permanently creates a physical table on disk",
                "It provides readable, modular named subqueries that can be referenced multiple times within the primary query",
                "It disables database transaction logs",
                "It converts SQL queries into Python pandas code automatically",
                1, QuestionDifficulty.INTERMEDIATE, "CTEs improve readability and maintainability of nested subqueries and recursive queries."));
        q.add(new CareerQuestion(c, "SQL", "What is the difference between RANK() and DENSE_RANK() in SQL window functions?",
                "RANK() leaves gaps in sequence when ties occur; DENSE_RANK() assigns consecutive rank values without gaps",
                "RANK() requires numeric columns; DENSE_RANK() is for strings",
                "DENSE_RANK() is non-deterministic",
                "There is no difference between them",
                0, QuestionDifficulty.ADVANCED, "RANK() skips numbers after ties (e.g., 1, 2, 2, 4); DENSE_RANK() does not skip (1, 2, 2, 3)."));
    }

    // -------------------------------------------------------------
    // 4. CLOUD ENGINEER (30 Questions: 6 Skills x 5 Questions)
    // -------------------------------------------------------------
    private void addCloudEngineerQuestions(List<CareerQuestion> q) {
        String c = "cloud-engineer";

        // Skill: Cloud fundamentals
        q.add(new CareerQuestion(c, "Cloud fundamentals", "Which cloud service model provides virtualized compute, storage, and networking hardware on demand?",
                "SaaS", "PaaS", "IaaS", "FaaS", 2, QuestionDifficulty.BEGINNER, "Infrastructure as a Service (IaaS) provides virtual machines, networks, and raw storage."));
        q.add(new CareerQuestion(c, "Cloud fundamentals", "What is the primary characteristic of cloud elasticity?",
                "The physical flexibility of server racks",
                "The ability of a system to dynamically allocate and de-allocate compute resources according to fluctuating real-time demand",
                "Storing backups in tape drives",
                "Encrypting network packets over satellite links",
                1, QuestionDifficulty.BEGINNER, "Elasticity allows workloads to scale resources up/down automatically to match demand."));
        q.add(new CareerQuestion(c, "Cloud fundamentals", "What is the difference between High Availability (HA) and Fault Tolerance (FT)?",
                "HA aims for minimal planned downtime (99.99%); FT ensures zero downtime or data loss through redundant hot spares at higher cost",
                "HA is for storage; FT is for compute only",
                "FT requires manual failover intervention by administrators",
                "Both terms mean exactly the same thing in cloud SLAs",
                0, QuestionDifficulty.INTERMEDIATE, "High Availability minimizes downtime via rapid failover; Fault Tolerance eliminates downtime with live duplicates."));
        q.add(new CareerQuestion(c, "Cloud fundamentals", "What is the purpose of Infrastructure as Code (IaC) tools like Terraform and AWS CloudFormation?",
                "To compile Java applications into Docker images",
                "To define, provision, and version infrastructure components declaratively using human-readable configuration files",
                "To monitor server CPU temperatures in real time",
                "To run continuous integration unit tests on client browsers",
                1, QuestionDifficulty.INTERMEDIATE, "IaC enables repeatable, declarative, and version-controlled infrastructure provisioning."));
        q.add(new CareerQuestion(c, "Cloud fundamentals", "What is the Shared Responsibility Model in public cloud computing?",
                "The cloud provider is responsible for everything including user passwords",
                "The provider manages security 'OF' the cloud (hardware, datacenters); the customer manages security 'IN' the cloud (OS patches, data, IAM)",
                "Customers are only responsible for paying monthly invoices",
                "The model only applies to private on-premise datacenters",
                1, QuestionDifficulty.ADVANCED, "The Shared Responsibility Model delineates cloud provider vs tenant security duties."));

        // Skill: AWS
        q.add(new CareerQuestion(c, "AWS", "Which AWS service provides resizable compute capacity in the form of virtual server instances?",
                "Amazon S3", "Amazon EC2", "Amazon RDS", "AWS Lambda", 1, QuestionDifficulty.BEGINNER, "Amazon Elastic Compute Cloud (EC2) provides scalable virtual instances."));
        q.add(new CareerQuestion(c, "AWS", "Which AWS managed service provides scalable object storage accessed via REST endpoints?",
                "Amazon EBS", "Amazon S3", "Amazon EFS", "AWS Glue", 1, QuestionDifficulty.BEGINNER, "Simple Storage Service (S3) is an industry-standard object store."));
        q.add(new CareerQuestion(c, "AWS", "What is the function of an AWS Auto Scaling Group (ASG)?",
                "To automatically renew expired SSL certificates",
                "To dynamically maintain the desired number of healthy EC2 instances based on CPU utilization or scaling policies",
                "To transfer database tables to Google Cloud Platform",
                "To shut down all servers at midnight",
                1, QuestionDifficulty.INTERMEDIATE, "ASGs scale instance counts automatically to maintain availability and optimize cost."));
        q.add(new CareerQuestion(c, "AWS", "What is AWS IAM (Identity and Access Management) primarily used for?",
                "Creating responsive frontend HTML pages",
                "Managing user access, roles, federated identities, and granular permissions for AWS service resources",
                "Configuring hardware cooling fans in AWS datacenters",
                "Scanning Docker images for compilation errors",
                1, QuestionDifficulty.INTERMEDIATE, "IAM manages authentication and role-based authorization policies across AWS."));
        q.add(new CareerQuestion(c, "AWS", "How do AWS Availability Zones (AZs) provide disaster resilience within a single AWS Region?",
                "AZs are located in different countries on separate continents",
                "AZs consist of one or more discrete datacenters with redundant power, networking, and connectivity, isolated from faults in other AZs",
                "AZs share a single physical electrical grid and cooling pipe",
                "AZs can only run serverless Lambda functions",
                1, QuestionDifficulty.ADVANCED, "AZs are isolated fault domains interconnected with low-latency private fiber."));

        // Skill: Networking
        q.add(new CareerQuestion(c, "Networking", "What protocol is used to translate domain names (e.g., example.com) into IP addresses?",
                "DHCP", "DNS", "FTP", "SNMP", 1, QuestionDifficulty.BEGINNER, "Domain Name System (DNS) resolves human-friendly names to IP addresses."));
        q.add(new CareerQuestion(c, "Networking", "Which OSI layer is responsible for logical IP addressing and packet routing across networks?",
                "Layer 1 (Physical)", "Layer 2 (Data Link)", "Layer 3 (Network)", "Layer 4 (Transport)", 2, QuestionDifficulty.BEGINNER, "Layer 3 (Network layer) routes packets across subnets using IP addresses."));
        q.add(new CareerQuestion(c, "Networking", "What is the primary difference between TCP and UDP?",
                "TCP is connection-oriented and guarantees delivery and ordering; UDP is connectionless and prioritizes low latency without guaranteed delivery",
                "UDP encrypts all packets; TCP does not",
                "TCP can only be used on local area networks",
                "UDP requires a 3-way handshake before sending bytes",
                0, QuestionDifficulty.INTERMEDIATE, "TCP guarantees ordered, reliable transmission; UDP provides fast, unacknowledged datagrams."));
        q.add(new CareerQuestion(c, "Networking", "What does a CIDR prefix of /24 represent in IPv4 networking?",
                "256 total IP addresses (254 usable host addresses on a standard subnet)",
                "65,536 total IP addresses",
                "1 single static IP address",
                "An IPv6 address translated to IPv4",
                0, QuestionDifficulty.INTERMEDIATE, "A /24 subnet mask has 8 host bits (2^8 = 256 addresses, minus network and broadcast)."));
        q.add(new CareerQuestion(c, "Networking", "What is the difference between a forward proxy and a reverse proxy?",
                "A forward proxy sits in front of backend servers; a reverse proxy sits in front of client browsers",
                "A forward proxy acts on behalf of clients to access the internet; a reverse proxy acts on behalf of servers to handle incoming client traffic",
                "Forward proxies only handle FTP traffic",
                "There is no difference; both are identical network devices",
                1, QuestionDifficulty.ADVANCED, "Forward proxies protect and route client outbound requests; reverse proxies shield and balance server inbound traffic."));

        // Skill: Linux
        q.add(new CareerQuestion(c, "Linux", "Which Linux command displays the current working directory?",
                "cd", "pwd", "ls", "whoami", 1, QuestionDifficulty.BEGINNER, "pwd stands for 'print working directory'."));
        q.add(new CareerQuestion(c, "Linux", "Which command is used to modify file and directory permissions in Linux?",
                "chown", "chmod", "ps", "kill", 1, QuestionDifficulty.BEGINNER, "chmod changes access permissions of file system objects."));
        q.add(new CareerQuestion(c, "Linux", "What does the command 'ps aux | grep java' accomplish?",
                "It installs the Java Development Kit on the server",
                "It lists all running system processes and filters the output for processes matching the word 'java'",
                "It deletes all Java class files in root directory",
                "It compiles a Java application from source",
                1, QuestionDifficulty.INTERMEDIATE, "Piping ps aux through grep filters running processes by keyword."));
        q.add(new CareerQuestion(c, "Linux", "What is the difference between a hard link and a soft (symbolic) link in Linux?",
                "A hard link points directly to the file inode; a soft link is a special file pointing to the pathname of the target",
                "Soft links cannot point to directories; hard links can span multiple filesystems",
                "Hard links are deleted if the original file is renamed",
                "Soft links take 10x more storage than the original file",
                0, QuestionDifficulty.INTERMEDIATE, "Hard links reference the underlying inode; symlinks store path references."));
        q.add(new CareerQuestion(c, "Linux", "Which Linux utility is best suited for real-time monitoring of disk I/O bottlenecks per device?",
                "top", "iostat / iotop", "netstat", "uname", 1, QuestionDifficulty.ADVANCED, "iostat and iotop report detailed input/output device and partition utilization."));

        // Skill: Docker
        q.add(new CareerQuestion(c, "Docker", "Which Docker command lists all currently running containers?",
                "docker images", "docker ps", "docker build", "docker pull", 1, QuestionDifficulty.BEGINNER, "docker ps lists active running containers."));
        q.add(new CareerQuestion(c, "Docker", "What is the purpose of the EXPOSE instruction in a Dockerfile?",
                "It automatically publishes ports to the host machine without any -p flags",
                "It documents the network ports that the container listens on at runtime",
                "It shuts down the container if the port is busy",
                "It opens firewall rules on the physical router",
                1, QuestionDifficulty.BEGINNER, "EXPOSE acts as metadata/documentation indicating intended container listening ports."));
        q.add(new CareerQuestion(c, "Docker", "What is the difference between a Docker image and a Docker container?",
                "An image is a running process; a container is the static template file",
                "An image is a read-only template with application code and dependencies; a container is a running, writable instance of that image",
                "Images are only stored in RAM; containers are stored on tape backup",
                "Containers cannot run without a hypervisor VM like VMware",
                1, QuestionDifficulty.INTERMEDIATE, "Images are immutable blueprints; containers are isolated executing instances."));
        q.add(new CareerQuestion(c, "Docker", "What is the primary benefit of multi-stage Docker builds?",
                "They increase the size of the final image for auditing",
                "They separate the build-time SDK environment from the lean runtime environment, producing minimal, secure production images",
                "They compile code into multiple programming languages simultaneously",
                "They automatically start a Kubernetes cluster on the host",
                1, QuestionDifficulty.INTERMEDIATE, "Multi-stage builds discard build tools and intermediate artifacts, keeping runtime images slim."));
        q.add(new CareerQuestion(c, "Docker", "What underlying Linux kernel primitives provide resource isolation and limits for Docker containers?",
                "cgroups (control groups) for resource limiting and namespaces for process/network isolation",
                "VirtualBox and VMware hardware virtualization extensions",
                "SELinux exclusively with no kernel support",
                "Systemd services running inside chroot jails without kernel hooks",
                0, QuestionDifficulty.ADVANCED, "Containers leverage Linux namespaces (isolation) and cgroups (CPU/memory throttling)."));

        // Skill: Security fundamentals
        q.add(new CareerQuestion(c, "Security fundamentals", "What is the principle of least privilege (PoLP) in cloud security?",
                "Giving all developers administrative root access to increase productivity",
                "Granting users and services only the minimum permissions strictly necessary to perform their required tasks",
                "Disabling all firewall rules in development environments",
                "Using simple passwords so users don't forget credentials",
                1, QuestionDifficulty.BEGINNER, "Least privilege minimizes blast radius by restricting access to strictly necessary actions."));
        q.add(new CareerQuestion(c, "Security fundamentals", "What is Multi-Factor Authentication (MFA)?",
                "Requiring users to enter multiple passwords simultaneously",
                "An authentication method requiring two or more distinct verification factors (something you know, have, or are)",
                "Encrypting network packets twice with different keys",
                "Logging into multiple accounts at the same time",
                1, QuestionDifficulty.BEGINNER, "MFA combines knowledge, possession, or inherence factors to prevent credential theft."));
        q.add(new CareerQuestion(c, "Security fundamentals", "What is the difference between encryption in transit and encryption at rest?",
                "In transit protects data moving over networks (TLS/SSL); at rest protects persisted data stored on disk (AES-256)",
                "At rest is for RAM; in transit is for SSDs",
                "Encryption in transit can only be used with HTTP, not HTTPS",
                "There is no difference; both use identical mechanisms",
                0, QuestionDifficulty.INTERMEDIATE, "Transit encryption secures network payloads; at-rest encryption protects persisted volumes and files."));
        q.add(new CareerQuestion(c, "Security fundamentals", "What is a security group in cloud virtual networks?",
                "A company committee that reviews security policies",
                "A stateful virtual firewall that controls inbound and outbound traffic to cloud compute instances",
                "A group of IAM users sharing the same password",
                "A physical guard patrolling the cloud datacenter",
                1, QuestionDifficulty.INTERMEDIATE, "Security groups act as stateful firewalls inspecting traffic at the instance network interface."));
        q.add(new CareerQuestion(c, "Security fundamentals", "What is the Zero Trust security architecture model?",
                "Trusting all internal devices inside the corporate perimeter without verification",
                "A security philosophy requiring strict identity verification and continuous authorization for every user and device, assuming breach",
                "Deleting all firewall logs to prevent unauthorized access",
                "Banning remote workers from accessing cloud workloads",
                1, QuestionDifficulty.ADVANCED, "Zero Trust operates on 'never trust, always verify' across internal and external boundaries."));
    }

    // -------------------------------------------------------------
    // 5. AI/ML ENGINEER (30 Questions: 6 Skills x 5 Questions)
    // -------------------------------------------------------------
    private void addAimLEngineerQuestions(List<CareerQuestion> q) {
        String c = "aiml-engineer";

        // Skill: Python for AI
        q.add(new CareerQuestion(c, "Python for AI", "What is the fundamental n-dimensional array object in NumPy?",
                "np.list", "np.ndarray", "np.matrix_2d", "np.dataframe", 1, QuestionDifficulty.BEGINNER, "ndarray is the foundational homogenous array object in NumPy."));
        q.add(new CareerQuestion(c, "Python for AI", "In PyTorch, what method triggers backward pass gradient computation for a scalar loss tensor?",
                "loss.compute()", "loss.backward()", "loss.gradient()", "loss.zero_grad()", 1, QuestionDifficulty.BEGINNER, "loss.backward() computes gradients via automatic differentiation (autograd)."));
        q.add(new CareerQuestion(c, "Python for AI", "Why is optimizer.zero_grad() called before backward() in standard PyTorch training loops?",
                "To release GPU memory completely",
                "Because PyTorch accumulates gradients in buffers by default across iterations rather than overwriting them",
                "To reset model weights back to random initialization",
                "To evaluate test accuracy on the validation split",
                1, QuestionDifficulty.INTERMEDIATE, "PyTorch accumulates gradients on .backward() calls; zero_grad() prevents stale accumulation."));
        q.add(new CareerQuestion(c, "Python for AI", "What does torch.no_grad() context manager accomplish during inference?",
                "It disables all Python error handling",
                "It disables autograd gradient tracking, saving significant GPU memory and accelerating forward pass evaluation",
                "It quantizes float32 weights into int8 precision",
                "It prevents models from processing batches larger than 1",
                1, QuestionDifficulty.INTERMEDIATE, "no_grad() deactivates autograd history tracking, reducing memory footprint during validation."));
        q.add(new CareerQuestion(c, "Python for AI", "How does CUDA memory management with torch.cuda.empty_cache() affect PyTorch execution?",
                "It frees all initialized neural network model weights immediately",
                "It releases cached unused GPU memory back to the operating system driver without clearing model parameters",
                "It forces CPU fallback for all tensor operations",
                "It resets CUDA kernel execution clocks",
                1, QuestionDifficulty.ADVANCED, "empty_cache() flushes the PyTorch caching allocator so other processes can use cached VRAM."));

        // Skill: Mathematics & Linear Algebra
        q.add(new CareerQuestion(c, "Mathematics & Linear Algebra", "What is the dot product of two orthogonal vectors?",
                "1", "0", "-1", "Infinity", 1, QuestionDifficulty.BEGINNER, "Orthogonal (perpendicular) vectors have a dot product of zero."));
        q.add(new CareerQuestion(c, "Mathematics & Linear Algebra", "What is an identity matrix?",
                "A matrix containing only zeros",
                "A square matrix with ones on the main diagonal and zeros elsewhere",
                "A matrix with equal row and column sums",
                "A matrix where all values equal 1.0",
                1, QuestionDifficulty.BEGINNER, "Identity matrices leave any multiplied vector or matrix unchanged."));
        q.add(new CareerQuestion(c, "Mathematics & Linear Algebra", "What are eigenvalues and eigenvectors in linear transformations (A v = lambda v)?",
                "Random parameters used in linear regression gradient descent",
                "Vectors whose direction does not change under transformation A, scaled only by the scalar factor lambda (eigenvalue)",
                "Coefficients for polynomial feature expansions",
                "Gradients computed during backpropagation",
                1, QuestionDifficulty.INTERMEDIATE, "Eigenvectors maintain their direction during linear transformation, scaled by eigenvalues."));
        q.add(new CareerQuestion(c, "Mathematics & Linear Algebra", "What is the purpose of the Jacobian matrix in multivariate calculus for ML?",
                "To store eigenvalues of a square covariance matrix",
                "To represent all first-order partial derivatives of a vector-valued function with respect to input parameters",
                "To invert singular non-invertible matrices",
                "To compute cosine similarity between word vectors",
                1, QuestionDifficulty.INTERMEDIATE, "Jacobian matrices compile all first-order partial derivatives for vector transformations."));
        q.add(new CareerQuestion(c, "Mathematics & Linear Algebra", "What does Singular Value Decomposition (SVD) decompose a real matrix A into?",
                "A = Q R where Q is orthogonal and R is upper triangular",
                "A = U Sigma V^T where U and V are orthogonal matrices and Sigma contains singular values",
                "A = L U where L is lower triangular and U is upper triangular",
                "A = P D P^-1 where D is diagonal",
                1, QuestionDifficulty.ADVANCED, "SVD factorizes arbitrary m x n matrices into orthogonal and diagonal singular value matrices."));

        // Skill: Deep Learning & Neural Networks
        q.add(new CareerQuestion(c, "Deep Learning & Neural Networks", "What is the purpose of an activation function (e.g. ReLU) in a neural network?",
                "To normalize input image resolution",
                "To introduce non-linearity, enabling the network to learn complex non-linear decision boundaries",
                "To save model weights to disk",
                "To enforce equal learning rates across all layers",
                1, QuestionDifficulty.BEGINNER, "Without non-linear activations, multi-layer networks collapse to a single linear transformation."));
        q.add(new CareerQuestion(c, "Deep Learning & Neural Networks", "What problem does the ReLU activation function mitigate compared to Sigmoid in deep networks?",
                "Exploding gradients in the output layer",
                "Vanishing gradient problem for positive input values",
                "Overfitting on small tabular datasets",
                "Slow forward pass inference on CPUs",
                1, QuestionDifficulty.BEGINNER, "ReLU has constant gradient 1 for positive inputs, avoiding sigmoid gradient saturation."));
        q.add(new CareerQuestion(c, "Deep Learning & Neural Networks", "What is the function of Dropout layers during neural network training?",
                "To permanently delete half of the network weights before deployment",
                "To randomly deactivate a fraction of neurons during training to prevent co-adaptation and reduce overfitting",
                "To increase learning rate when loss plateaus",
                "To drop invalid image files from batch loaders",
                1, QuestionDifficulty.INTERMEDIATE, "Dropout regularizes models by preventing interdependent co-adaptations between hidden units."));
        q.add(new CareerQuestion(c, "Deep Learning & Neural Networks", "What is the key structural innovation of Residual Networks (ResNet)?",
                "Replacing all convolution filters with fully connected layers",
                "Skip (residual) connections that allow gradients to propagate directly through identity shortcuts, enabling training of very deep networks",
                "Using exclusively recurrent connections between layers",
                "Removing backpropagation in favor of genetic algorithms",
                1, QuestionDifficulty.INTERMEDIATE, "Residual connections bypass layers via identity additions, overcoming vanishing gradients in 100+ layer nets."));
        q.add(new CareerQuestion(c, "Deep Learning & Neural Networks", "How does Batch Normalization stabilize deep network optimization?",
                "By sorting training samples by loss value",
                "By normalizing layer inputs across the mini-batch to have zero mean and unit variance, reducing internal covariate shift",
                "By converting all weights to int8 precision",
                "By disabling gradient backpropagation for every second batch",
                1, QuestionDifficulty.ADVANCED, "BatchNorm re-centers and re-scales layer activations to accelerate and stabilize convergence."));

        // Skill: Model Evaluation
        q.add(new CareerQuestion(c, "Model Evaluation", "What does Accuracy measure in a binary classification task?",
                "The ratio of True Positives to all actual positive cases",
                "The ratio of correct predictions (True Positives + True Negatives) to total predictions",
                "The area under the precision-recall curve",
                "The speed of inference in milliseconds",
                1, QuestionDifficulty.BEGINNER, "Accuracy is the proportion of total correct classifications over all examples."));
        q.add(new CareerQuestion(c, "Model Evaluation", "Why is Accuracy a misleading evaluation metric for highly imbalanced datasets (e.g. 99% negative fraud detection)?",
                "It takes too long to compute on large datasets",
                "A naive model predicting always 'negative' achieves 99% accuracy while detecting zero true fraud cases",
                "Accuracy cannot be computed with floating-point probabilities",
                "Accuracy is only applicable to regression problems",
                1, QuestionDifficulty.BEGINNER, "In imbalanced data, high accuracy masks failure to detect rare positive target classes."));
        q.add(new CareerQuestion(c, "Model Evaluation", "What is the difference between Precision and Recall?",
                "Precision is TP/(TP+FP) measuring correctness of positive predictions; Recall is TP/(TP+FN) measuring ability to find all actual positives",
                "Precision measures negative cases; Recall measures positive cases",
                "Precision is used for regression; Recall is used for clustering",
                "Both formulas are identical",
                0, QuestionDifficulty.INTERMEDIATE, "Precision measures predictive purity; Recall measures sensitivity/coverage of positive targets."));
        q.add(new CareerQuestion(c, "Model Evaluation", "What does the Area Under the ROC Curve (AUC-ROC) represent?",
                "The training loss value of the model",
                "The probability that the model ranks a randomly chosen positive instance higher than a randomly chosen negative instance across all classification thresholds",
                "The speed of model convergence in epochs",
                "The percentage of missing values in the dataset",
                1, QuestionDifficulty.INTERMEDIATE, "AUC-ROC evaluates aggregate discriminative power across all possible threshold cutoffs."));
        q.add(new CareerQuestion(c, "Model Evaluation", "What is the F1-Score and when is it preferred over simple average of Precision and Recall?",
                "It is the arithmetic mean; used when data is perfectly balanced",
                "It is the harmonic mean of Precision and Recall; penalizes extreme discrepancies between precision and recall",
                "It is the maximum possible score an algorithm can achieve",
                "It measures inference latency on mobile edge chips",
                1, QuestionDifficulty.ADVANCED, "The harmonic mean pulls the F1 score toward the lower value if precision or recall collapses."));

        // Skill: NLP & Computer Vision
        q.add(new CareerQuestion(c, "NLP & Computer Vision", "What does tokenization perform in natural language processing (NLP)?",
                "It encrypts text messages for database storage",
                "It splits raw text strings into smaller individual units such as words, subwords, or characters",
                "It automatically translates text between languages",
                "It removes all punctuation marks permanently",
                1, QuestionDifficulty.BEGINNER, "Tokenization breaks continuous text into discrete token identifiers for vocabulary mapping."));
        q.add(new CareerQuestion(c, "NLP & Computer Vision", "Which neural network architecture is primarily used for spatial feature extraction in image processing?",
                "Recurrent Neural Network (RNN)", "Convolutional Neural Network (CNN)", "Multilayer Perceptron (MLP)", "K-Means", 1, QuestionDifficulty.BEGINNER, "CNNs use convolutional kernels to extract translation-invariant spatial visual patterns."));
        q.add(new CareerQuestion(c, "NLP & Computer Vision", "What is the core mechanism of the Transformer architecture (Vaswani et al.)?",
                "Recurrent feedback loops with LSTM gates",
                "Self-Attention (Scaled Dot-Product Attention) allowing parallelized modeling of token relationships across entire sequences",
                "Dynamic time warping algorithms",
                "Genetic mutation of attention weights",
                1, QuestionDifficulty.INTERMEDIATE, "Self-attention replaces sequential recurrence with parallel pairwise token correlation matrices."));
        q.add(new CareerQuestion(c, "NLP & Computer Vision", "What is Transfer Learning in computer vision and NLP?",
                "Transferring model files between two hard drives",
                "Fine-tuning a model pre-trained on a massive generic dataset (e.g. ImageNet, CommonCrawl) on a specific downstream task with smaller labeled data",
                "Converting PyTorch code into TensorFlow code",
                "Translating spoken audio into subtitles",
                1, QuestionDifficulty.INTERMEDIATE, "Transfer learning adapts pre-trained representations to specialized tasks with higher efficiency."));
        q.add(new CareerQuestion(c, "NLP & Computer Vision", "What is the function of Positional Encoding in Transformer models?",
                "To count the number of words in a paragraph",
                "To inject order and sequence position information into token embeddings since self-attention is inherently permutation-invariant",
                "To normalize pixel brightness values in images",
                "To encrypt token IDs before passing to the GPU",
                1, QuestionDifficulty.ADVANCED, "Since Transformers process tokens in parallel, positional embeddings restore token ordering awareness."));

        // Skill: MLOps
        q.add(new CareerQuestion(c, "MLOps", "What is the primary role of MLflow or Weights & Biases in machine learning engineering?",
                "To build frontend dashboard web templates",
                "To track experiment parameters, metrics, hyperparameters, and version model artifacts systematically",
                "To clean corrupted image files from disk",
                "To replace Python with C++ code",
                1, QuestionDifficulty.BEGINNER, "Experiment tracking tools record hyperparameters, loss curves, and artifact versions."));
        q.add(new CareerQuestion(c, "MLOps", "What is 'data drift' in production machine learning systems?",
                "When database servers physically relocate to other datacenters",
                "When the statistical distribution of input data changes over time in production, causing model performance degradation",
                "When hard drives run out of storage space",
                "When data is backed up to multiple cloud regions",
                1, QuestionDifficulty.BEGINNER, "Data drift occurs when production distribution deviates from the training baseline distribution."));
        q.add(new CareerQuestion(c, "MLOps", "What is the purpose of ONNX (Open Neural Network Exchange)?",
                "To compile HTML into CSS stylesheets",
                "An open format representing machine learning models to enable interoperability and optimized execution across different frameworks and hardware runtimes",
                "A cloud payment gateway for AI APIs",
                "A library for scraping web pages",
                1, QuestionDifficulty.INTERMEDIATE, "ONNX provides a framework-agnostic computation graph format for cross-platform model deployment."));
        q.add(new CareerQuestion(c, "MLOps", "What is model quantization (e.g. FP32 to INT8) in edge deployment pipelines?",
                "Training a model for 8 times more epochs",
                "Reducing weight and activation numerical precision from 32-bit floats to 8-bit integers, drastically reducing memory footprint and speeding inference with minimal accuracy loss",
                "Deleting all negative weights from layers",
                "Splitting model layers across 8 separate servers",
                1, QuestionDifficulty.INTERMEDIATE, "Quantization compresses model memory requirements and accelerates integer matrix arithmetic on hardware."));
        q.add(new CareerQuestion(c, "MLOps", "In CI/CD for machine learning (CT - Continuous Training), what triggers automated retraining pipelines?",
                "Only annual manual calendar reminders",
                "Automated alerts on data/concept drift metrics, degradation of inference performance against ground truth, or new scheduled batch arrivals",
                "Every single user API request to the backend",
                "Restarting the Docker host operating system",
                1, QuestionDifficulty.ADVANCED, "Continuous training pipelines trigger automatically based on drift metrics, performance thresholds, or schedule."));
    }

    // -------------------------------------------------------------
    // 6. CYBERSECURITY ANALYST (30 Questions: 6 Skills x 5 Questions)
    // -------------------------------------------------------------
    private void addCybersecurityQuestions(List<CareerQuestion> q) {
        String c = "cybersecurity-analyst";

        // Skill: Network Security
        q.add(new CareerQuestion(c, "Network Security", "Which security appliance inspects network packets and blocks unauthorized traffic based on configured security rules?",
                "Repeater", "Firewall", "Hub", "Unmanaged Switch", 1, QuestionDifficulty.BEGINNER, "Firewalls enforce access control policies on incoming and outgoing network traffic."));
        q.add(new CareerQuestion(c, "Network Security", "What type of cyber attack floods a target server with bogus traffic to exhaust resources and cause unavailability?",
                "Phishing", "Denial of Service (DoS / DDoS)", "SQL Injection", "Cross-Site Scripting", 1, QuestionDifficulty.BEGINNER, "DDoS attacks overwhelm network bandwidth or application resources with distributed traffic."));
        q.add(new CareerQuestion(c, "Network Security", "What is the difference between an IDS (Intrusion Detection System) and an IPS (Intrusion Prevention System)?",
                "An IDS monitors and alerts on detected threats; an IPS actively intervenes inline to drop malicious packets and block traffic",
                "An IDS encrypts data; an IPS decrypts data",
                "An IDS is for wireless networks only; an IPS is for wired networks",
                "Both systems are identical in architecture and operation",
                0, QuestionDifficulty.INTERMEDIATE, "IDS provides passive alert monitoring; IPS performs active inline packet blocking and mitigation."));
        q.add(new CareerQuestion(c, "Network Security", "What security risk is introduced by an open, unsegmented 'flat network' architecture?",
                "It causes Ethernet cables to overheat",
                "An attacker who breaches one device can easily pivot laterally throughout the entire internal network without restriction",
                "It prevents web servers from responding to HTTP requests",
                "It disables antivirus software on client workstations",
                1, QuestionDifficulty.INTERMEDIATE, "Network segmentation confines breaches and restricts unauthorized lateral adversary movement."));
        q.add(new CareerQuestion(c, "Network Security", "How does 802.1X network access control protect enterprise local area networks?",
                "By encrypting optical fiber cables against wiretapping",
                "By requiring port-based authentication (RADIUS/EAP) of user credentials or device certificates before granting network connectivity",
                "By banning all Linux machines from connecting to WiFi",
                "By forcing all web traffic through unencrypted HTTP proxies",
                1, QuestionDifficulty.ADVANCED, "802.1X mandates device/user authentication before bridging switch or wireless access ports."));

        // Skill: Threat Analysis
        q.add(new CareerQuestion(c, "Threat Analysis", "What is a 'Zero-Day' vulnerability in cybersecurity?",
                "A vulnerability discovered exactly zero days after an operating system release",
                "A software flaw known to attackers or researchers for which no vendor patch or security fix currently exists",
                "A security test that takes zero seconds to complete",
                "A password that contains zero digits",
                1, QuestionDifficulty.BEGINNER, "Zero-day vulnerabilities have no official vendor patch available at the time of exposure."));
        q.add(new CareerQuestion(c, "Threat Analysis", "What is the MITRE ATT&CK framework primarily used for?",
                "Generating random cryptographic keys",
                "A globally accessible knowledge base of adversary tactics, techniques, and procedures (TTPs) based on real-world observations",
                "Writing automated penetration testing exploits",
                "Configuring network printer drivers",
                1, QuestionDifficulty.BEGINNER, "MITRE ATT&CK maps real-world cyber adversary tactics and techniques across attack lifecycles."));
        q.add(new CareerQuestion(c, "Threat Analysis", "What constitutes an Indicator of Compromise (IoC) in threat hunting?",
                "A marketing document detailing software pricing",
                "Forensic artifacts on a network or host that provide high-confidence evidence of an intrusion (e.g. file hashes, malicious IPs, registry keys)",
                "A newly purchased firewall appliance",
                "A user's forgotten password ticket",
                1, QuestionDifficulty.INTERMEDIATE, "IoCs are observable technical artifacts signaling active or past system compromise."));
        q.add(new CareerQuestion(c, "Threat Analysis", "What is the primary difference between a vulnerability, a threat, and a risk?",
                "A vulnerability is a weakness; a threat is a potential actor/event exploiting that weakness; risk is the probability and impact of exploitation",
                "A threat is a software bug; a vulnerability is an attacker; risk is the cost of antivirus software",
                "They are synonyms used interchangeably in risk assessment",
                "Risk only applies to physical corporate buildings",
                0, QuestionDifficulty.INTERMEDIATE, "Vulnerability is the flaw; threat is the potential adversary; risk is likelihood times impact."));
        q.add(new CareerQuestion(c, "Threat Analysis", "In the Cyber Kill Chain model, what occurs during the 'Command and Control' (C2) phase?",
                "The attacker delivers an email attachment to the victim",
                "The compromised host establishes an encrypted communication channel back to the attacker's infrastructure for remote tasking",
                "The attacker scans public IP ranges with Nmap",
                "The victim restores their backups from offline storage",
                1, QuestionDifficulty.ADVANCED, "C2 establishes persistent two-way communication channels allowing attackers to direct implants."));

        // Skill: Cryptography Basics
        q.add(new CareerQuestion(c, "Cryptography Basics", "Which type of cryptography uses a single shared secret key for both encryption and decryption?",
                "Asymmetric cryptography", "Symmetric cryptography", "Public key cryptography", "Hashing algorithms", 1, QuestionDifficulty.BEGINNER, "Symmetric encryption (e.g. AES) uses the same secret key for encryption and decryption."));
        q.add(new CareerQuestion(c, "Cryptography Basics", "What is a fundamental property of a cryptographic hash function (e.g. SHA-256)?",
                "It can easily be reversed back to original plain text",
                "It is a deterministic, one-way function that produces a fixed-length output hash from arbitrary-length inputs",
                "It requires a private key to compute",
                "It changes output length depending on input file size",
                1, QuestionDifficulty.BEGINNER, "Cryptographic hash functions are deterministic, collision-resistant, and non-reversible one-way functions."));
        q.add(new CareerQuestion(c, "Cryptography Basics", "In asymmetric public-key cryptography (e.g. RSA), which key is used to decrypt a message encrypted with Alice's public key?",
                "Alice's private key", "Alice's public key", "The sender's public key", "A symmetric session token", 0, QuestionDifficulty.INTERMEDIATE, "Data encrypted with a recipient's public key can only be decrypted by their corresponding private key."));
        q.add(new CareerQuestion(c, "Cryptography Basics", "What is the primary purpose of a Digital Signature in electronic communication?",
                "To compress large PDF files before emailing",
                "To provide message authenticity, integrity, and non-repudiation by signing a hash with a private key",
                "To encrypt all attachments using symmetric keys",
                "To automatically translate text to another language",
                1, QuestionDifficulty.INTERMEDIATE, "Digital signatures prove sender identity and guarantee the message content was not altered in transit."));
        q.add(new CareerQuestion(c, "Cryptography Basics", "What security risk does a 'salt' mitigate when storing hashed passwords in a database?",
                "It prevents SQL injection vulnerabilities",
                "It ensures identical passwords produce unique hash digests, defeating precomputed rainbow table attacks",
                "It compresses password hashes to save disk space",
                "It automatically changes user passwords every 90 days",
                1, QuestionDifficulty.ADVANCED, "Salting appends unique random data per user, preventing rainbow tables and collision attacks."));

        // Skill: Vulnerability Assessment
        q.add(new CareerQuestion(c, "Vulnerability Assessment", "What is the Common Vulnerabilities and Exposures (CVE) system?",
                "A paid certification for cybersecurity analysts",
                "A publicly standardized dictionary of known information-security vulnerabilities and exposures with unique identifiers",
                "A programming language for writing network drivers",
                "A government agency that investigates wire fraud",
                1, QuestionDifficulty.BEGINNER, "CVE provides standardized public reference identifiers for software and firmware vulnerabilities."));
        q.add(new CareerQuestion(c, "Vulnerability Assessment", "What does the Common Vulnerability Scoring System (CVSS) evaluate?",
                "The market financial valuation of a cybersecurity company",
                "The severity and technical characteristics of a software vulnerability on a numerical scale from 0.0 to 10.0",
                "The speed of network packet transmission over VPNs",
                "The number of lines of code in a web application",
                1, QuestionDifficulty.BEGINNER, "CVSS calculates severity metrics (Base, Temporal, Environmental) ranging from Low to Critical (10.0)."));
        q.add(new CareerQuestion(c, "Vulnerability Assessment", "What is the difference between a Vulnerability Scan and a Penetration Test?",
                "A vulnerability scan is automated and identifies potential flaws; a penetration test actively exploits vulnerabilities to evaluate defense depth",
                "A vulnerability scan is illegal; a penetration test is always legal",
                "A vulnerability scan is performed manually; penetration tests are purely automated bots",
                "There is no difference between them",
                0, QuestionDifficulty.INTERMEDIATE, "Vulnerability scanning surfaces suspected flaws; penetration testing actively verifies and exploits weaknesses."));
        q.add(new CareerQuestion(c, "Vulnerability Assessment", "What is the difference between authenticated (credentialed) and unauthenticated vulnerability scans?",
                "Authenticated scans log into target systems to audit internal software packages and configs; unauthenticated scans inspect exposed network ports from the outside",
                "Unauthenticated scans find more internal zero-day flaws than authenticated scans",
                "Authenticated scans can only run on Windows machines",
                "Unauthenticated scans require root passwords",
                0, QuestionDifficulty.INTERMEDIATE, "Credentialed scans inspect internal configurations, patch levels, and installed packages accurately."));
        q.add(new CareerQuestion(c, "Vulnerability Assessment", "In a web application security context, what vulnerability occurs when untrusted user input is directly concatenated into a dynamic SQL query?",
                "Cross-Site Scripting (XSS)", "SQL Injection (SQLi)", "Cross-Site Request Forgery (CSRF)", "Buffer Overflow", 1, QuestionDifficulty.ADVANCED, "SQL Injection allows attackers to manipulate query logic via unescaped string concatenation."));

        // Skill: Security Operations
        q.add(new CareerQuestion(c, "Security Operations", "What does a Security Operations Center (SOC) primarily do?",
                "Manufactures physical security cameras and locks",
                "Monitors, detects, analyzes, and responds to cybersecurity incidents across enterprise networks 24/7/365",
                "Manages customer billing disputes",
                "Installs fiber optic cables between office buildings",
                1, QuestionDifficulty.BEGINNER, "A SOC provides continuous monitoring, threat detection, and incident response operations."));
        q.add(new CareerQuestion(c, "Security Operations", "What does a SIEM (Security Information and Event Management) platform do?",
                "It replaces the need for network firewalls",
                "It aggregates, normalizes, correlates, and analyzes security event log data from across an enterprise in real time",
                "It automatically compiles web application code",
                "It resets employee passwords every morning",
                1, QuestionDifficulty.BEGINNER, "SIEM systems centralize log collection, correlation rule evaluation, and incident alert generation."));
        q.add(new CareerQuestion(c, "Security Operations", "What is a 'False Positive' alert in security event monitoring?",
                "An alert triggered by actual malicious activity that was ignored",
                "An alert triggered by benign, legitimate activity incorrectly identified as malicious",
                "A security incident that resulted in zero financial loss",
                "An encrypted packet that failed checksum verification",
                1, QuestionDifficulty.INTERMEDIATE, "False positives occur when harmless legitimate actions trigger security alerts, causing analyst fatigue."));
        q.add(new CareerQuestion(c, "Security Operations", "What is SOAR (Security Orchestration, Automation, and Response)?",
                "A flight simulator for cybersecurity training",
                "A technology solution that enables standardized playbooks to automate repetitive incident triage and remediation workflows across disparate tools",
                "A protocol used exclusively for satellite communication",
                "A database engine for storing employee attendance",
                1, QuestionDifficulty.INTERMEDIATE, "SOAR connects security tools and automates repetitive alert enrichment and remediation steps via playbooks."));
        q.add(new CareerQuestion(c, "Security Operations", "Why is log retention and time synchronization (NTP) critical for forensic investigations?",
                "To conserve hard drive space on web servers",
                "To reconstruct an accurate chronological timeline of adversary actions across disparate distributed systems during an investigation",
                "To prevent servers from generating log files on weekends",
                "To encrypt network traffic over HTTP ports",
                1, QuestionDifficulty.ADVANCED, "Synchronized timestamps (NTP) are vital for establishing chronological cross-system audit trails."));

        // Skill: Incident Response
        q.add(new CareerQuestion(c, "Incident Response", "According to NIST SP 800-61, what is the first phase in the incident response lifecycle?",
                "Eradication", "Preparation", "Containment", "Post-Incident Activity", 1, QuestionDifficulty.BEGINNER, "Preparation establishes policies, tools, communication plans, and team training before incidents occur."));
        q.add(new CareerQuestion(c, "Incident Response", "What is the main objective of the 'Containment' phase during a ransomware outbreak?",
                "To issue a public press release immediately",
                "To isolate infected hosts from the network to prevent lateral spread to other systems and critical data stores",
                "To permanently format all enterprise hard drives",
                "To contact the attacker and pay the requested ransom",
                1, QuestionDifficulty.BEGINNER, "Containment isolates compromised assets to stop the spread of malware and limit damage."));
        q.add(new CareerQuestion(c, "Incident Response", "What is the primary difference between 'Eradication' and 'Recovery' in incident response?",
                "Eradication removes malware, artifacts, and compromised accounts; Recovery restores clean systems to normal business operations and validates stability",
                "Recovery is performed before any containment measures",
                "Eradication is only for physical server hardware",
                "Both phases are executed simultaneously on production databases",
                0, QuestionDifficulty.INTERMEDIATE, "Eradication eliminates root causes and malicious remnants; Recovery safely brings clean systems back online."));
        q.add(new CareerQuestion(c, "Incident Response", "What is the purpose of maintaining a strict 'Chain of Custody' for digital forensic evidence?",
                "To speed up hard drive formatting",
                "To chronologically document the collection, handling, transfer, analysis, and storage of evidence to ensure legal admissibility in court",
                "To delete evidence after 30 days automatically",
                "To share malware samples on public social media channels",
                1, QuestionDifficulty.INTERMEDIATE, "Chain of custody proves evidence integrity and authenticity for legal and regulatory proceedings."));
        q.add(new CareerQuestion(c, "Incident Response", "What is the primary outcome of a 'Lessons Learned' (Post-Incident Review) meeting?",
                "To assign disciplinary blame to individual employees",
                "To evaluate incident response performance, identify gaps in tooling or procedures, and update defenses and playbooks to prevent recurrence",
                "To delete all incident logs from SIEM repositories",
                "To cancel cybersecurity insurance policies",
                1, QuestionDifficulty.ADVANCED, "Lessons Learned meetings identify procedural and technical improvements to strengthen future security posture."));
    }
}
