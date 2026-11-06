package com.skillbridge.config;

import com.skillbridge.dto.QuizQuestionCreateRequest;
import com.skillbridge.model.*;
import com.skillbridge.repository.CourseRepository;
import com.skillbridge.repository.LessonQuizQuestionRepository;
import com.skillbridge.repository.LessonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(3)
public class CourseDataInitializer implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LessonQuizQuestionRepository quizQuestionRepository;

    public CourseDataInitializer(
            CourseRepository courseRepository,
            LessonRepository lessonRepository,
            LessonQuizQuestionRepository quizQuestionRepository
    ) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.quizQuestionRepository = quizQuestionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (courseRepository.count() >= 32) {
            return; // Already seeded
        }

        seedJavaDeveloperCourses();
        seedFullStackDeveloperCourses();
        seedDataScientistCourses();
        seedCloudEngineerCourses();
        seedAiMlEngineerCourses();
        seedCybersecurityCourses();
    }

    // =========================================================================
    // 1. JAVA DEVELOPER COURSES (6 Courses)
    // =========================================================================
    private void seedJavaDeveloperCourses() {
        // Course 1: Java Fundamentals
        Course c1 = saveCourse("java-fundamentals", "Java Fundamentals",
                "Master core Java syntax, JVM architecture, data types, control flow, loops, methods, and memory management for modern backend software development.",
                "java-developer", "Java Developer", "Java Fundamentals", QuestionDifficulty.BEGINNER, "3 hours", "bi-filetype-java", "primary");
        addLesson(c1, 1, "JVM Architecture, Data Types & Variables", 25,
                "• Understand JVM, JRE, and JDK roles\n• Learn primitive vs reference types\n• Master variable scoping and memory layout",
                "Java is a strongly typed, object-oriented language that runs on billions of devices worldwide. Understanding the Java Virtual Machine (JVM) is foundational to writing performant, resilient applications.",
                "### JVM, JRE, and JDK Ecosystem\n\n- **JDK (Java Development Kit)**: Contains the compiler (javac), debugging tools, and the JRE.\n- **JRE (Java Runtime Environment)**: Provides libraries and the JVM to run bytecode.\n- **JVM (Java Virtual Machine)**: Executes bytecode `.class` files, handling JIT compilation, garbage collection, and platform abstraction ('write once, run anywhere').\n\n### Memory Model: Stack vs Heap\n\n1. **Stack Memory**: Stores primitive local variables and method call execution frames. Highly optimized LIFO structure managed automatically.\n2. **Heap Memory**: Stores objects and instance variables created with `new`. Subject to JVM Garbage Collection (GC).\n\n### Primitive vs Reference Types\n\nJava features 8 primitives: `byte`, `short`, `int`, `long`, `float`, `double`, `boolean`, `char`. Reference types point to objects on the Heap.",
                "public class BasicsDemo {\n    public static void main(String[] args) {\n        // Primitive variables (stored on the thread stack)\n        int count = 42;\n        double price = 19.99;\n        boolean active = true;\n\n        // Reference variable (reference on stack, object on Heap)\n        String message = new String(\"Hello SkillBridge!\");\n\n        System.out.printf(\"Count: %d, Price: %.2f, Active: %b%n\", count, price, active);\n        System.out.println(\"Message: \" + message);\n    }\n}",
                "• Primitives are stored by value; references store memory addresses.\n• `String` is immutable — modifications produce new heap allocations.\n• Always choose appropriate numeric types to avoid integer overflow.",
                "Java bytecode runs on the JVM. Stack memory handles method call frames and primitives, while Heap memory hosts dynamically allocated objects subject to garbage collection.",
                "1. Why does Java use bytecode instead of compiling directly to native machine instructions?\n2. What occurs when a primitive variable is assigned to another primitive variable?",
                List.of(
                        new QuizQuestionCreateRequest(1, "Which JVM component is responsible for executing bytecode instructions and reclaiming unused memory?", "Javac Compiler", "Garbage Collector & Execution Engine", "JDK Build Pack", "Metaspace Archive", 1, "The JVM Execution Engine runs bytecode, and the Garbage Collector reclaims unused heap memory."),
                        new QuizQuestionCreateRequest(2, "Where are local primitive variables stored during method execution?", "Heap Memory", "Stack Memory", "Metaspace", "Permanent Generation", 1, "Local primitive variables and method call frames are allocated on the Stack.")
                ));

        addLesson(c1, 2, "Control Flow, Branching & Loops", 20,
                "• Master if-else and modern switch expressions\n• Write structured for, enhanced-for, and while loops\n• Understand loop boundary conditions and jump statements",
                "Control structures direct the logical flow of program execution based on conditions and iterative data requirements.",
                "### Modern Control Flow in Java\n\nJava 17 introduced switch expressions with arrow syntax (`->`), yield values, and pattern matching benefits.\n\n### Looping Constructs\n\n- **For loop**: Best when iteration count is known in advance.\n- **Enhanced for-each loop**: Ideal for traversing arrays and Iterable collections without index management.\n- **While and Do-While**: Used when termination depends on runtime states.",
                "public class ControlFlowDemo {\n    public static void main(String[] args) {\n        int dayOfWeek = 3;\n        // Modern Java 17 Switch Expression\n        String type = switch (dayOfWeek) {\n            case 1, 7 -> \"Weekend\";\n            case 2, 3, 4, 5, 6 -> \"Weekday\";\n            default -> \"Invalid day\";\n        };\n        System.out.println(\"Day type: \" + type);\n\n        // Enhanced For Loop\n        int[] numbers = {10, 20, 30, 40};\n        for (int num : numbers) {\n            if (num == 30) continue;\n            System.out.println(\"Number: \" + num);\n        }\n    }\n}",
                "• Modern switch expressions do not fall through by default (no break required with `->`).\n• Avoid modifying collections while iterating over them with an enhanced for loop to prevent `ConcurrentModificationException`.",
                "Structured branching with switch expressions and clean loop iteration enables readable, bug-free procedural logic.",
                "1. How does switch expression syntax prevent fall-through bugs?\n2. What is the difference between break and continue?",
                List.of(
                        new QuizQuestionCreateRequest(1, "What happens in a Java 17 arrow switch expression (`case ->`) if no break is supplied?", "It automatically falls through to the next case", "It executes only that specific branch without falling through", "The compiler raises a syntax error", "It halts program execution", 1, "Arrow syntax (`->`) in modern switch expressions does not fall through."),
                        new QuizQuestionCreateRequest(2, "Which statement skips the current iteration and jumps to the next loop cycle?", "break", "continue", "yield", "return", 1, "The continue statement skips the remainder of the current iteration.")
                ));

        // Course 2: Object-Oriented Programming
        Course c2 = saveCourse("object-oriented-programming", "Object-Oriented Programming",
                "Learn the four pillars of OOP: Encapsulation, Inheritance, Polymorphism, and Abstraction, along with SOLID design principles and composition patterns.",
                "java-developer", "Java Developer", "OOP", QuestionDifficulty.BEGINNER, "3.5 hours", "bi-boxes", "primary");
        addLesson(c2, 1, "The Four Pillars of Object-Oriented Programming", 30,
                "• Master Encapsulation and data hiding\n• Understand Inheritance and the 'is-a' relationship\n• Implement Polymorphism and Dynamic Method Dispatch\n• Use Interfaces and Abstract Classes for Abstraction",
                "Object-Oriented Programming models software as interacting components holding state and behavior, enabling scalable enterprise software architecture.",
                "### Core Pillars\n\n1. **Encapsulation**: Bundling fields and methods within a class while restricting direct attribute access via private modifiers and getters/setters.\n2. **Inheritance**: Subclasses inherit behavior and state from a superclass using `extends`.\n3. **Polymorphism**: The ability of an object to take many forms. Runtime polymorphism allows calling overridden methods dynamically.\n4. **Abstraction**: Hiding implementation details and exposing only essential contracts through `abstract` classes and `interface` contracts.",
                "// Abstraction\ninterface PaymentProcessor {\n    void process(double amount);\n}\n\n// Encapsulation and Polymorphism\nclass CreditCardPayment implements PaymentProcessor {\n    private String cardNumber;\n\n    public CreditCardPayment(String cardNumber) {\n        this.cardNumber = cardNumber;\n    }\n\n    @Override\n    public void process(double amount) {\n        System.out.printf(\"Processing $%.2f via Card [****%s]%n\", amount, cardNumber.substring(cardNumber.length() - 4));\n    }\n}\n\npublic class OopDemo {\n    public static void main(String[] args) {\n        PaymentProcessor processor = new CreditCardPayment(\"1234567812345678\");\n        processor.process(250.0);\n    }\n}",
                "• Favor composition over inheritance to avoid brittle superclass coupling.\n• Polymorphism enables open/closed design where new implementations can be introduced without changing client code.",
                "Encapsulation protects invariants, abstraction defines behavioral contracts, inheritance shares common structure, and polymorphism allows interchangeable runtime behavior.",
                "1. Can a class implement multiple interfaces in Java? Why?\n2. What is the difference between abstract classes and interfaces?",
                List.of(
                        new QuizQuestionCreateRequest(1, "Which OOP pillar hides object internal state and exposes interaction only through public methods?", "Inheritance", "Polymorphism", "Encapsulation", "Abstraction", 2, "Encapsulation bundles data and restricts direct field modification."),
                        new QuizQuestionCreateRequest(2, "What enables calling an overridden method on a subclass through a superclass reference variable at runtime?", "Method Overloading", "Dynamic Method Dispatch (Polymorphism)", "Static Binding", "Encapsulation", 1, "Dynamic method dispatch executes the subclass override at runtime.")
                ));

        addLesson(c2, 2, "SOLID Principles and Clean Architecture", 25,
                "• Learn Single Responsibility and Open/Closed principles\n• Understand Liskov Substitution and Interface Segregation\n• Apply Dependency Inversion in Java",
                "The SOLID principles guide developers in creating maintainable, testable, and loosely coupled codebases.",
                "### The SOLID Acronym\n\n- **S - Single Responsibility Principle**: A class should have one, and only one, reason to change.\n- **O - Open/Closed Principle**: Classes should be open for extension, but closed for modification.\n- **L - Liskov Substitution Principle**: Subclasses must be substitutable for their supertypes without breaking correctness.\n- **I - Interface Segregation Principle**: Prefer many client-specific interfaces over one bulky general-purpose interface.\n- **D - Dependency Inversion Principle**: Depend on abstractions, not on concrete implementations.",
                "// Dependency Inversion: High-level service depends on abstraction\ninterface NotificationService {\n    void send(String recipient, String message);\n}\n\nclass EmailService implements NotificationService {\n    public void send(String to, String msg) {\n        System.out.println(\"Emailing \" + to + \": \" + msg);\n    }\n}\n\nclass OrderManager {\n    private final NotificationService notifier;\n\n    // Injected via constructor\n    public OrderManager(NotificationService notifier) {\n        this.notifier = notifier;\n    }\n\n    public void placeOrder(String user) {\n        System.out.println(\"Order placed for \" + user);\n        notifier.send(user, \"Your order is confirmed!\");\n    }\n}",
                "• Dependency Injection in Spring Boot is an implementation of the Dependency Inversion Principle.\n• Breaking interfaces into focused contracts makes unit testing with mocks effortless.",
                "Applying SOLID principles produces modular components that are easy to test, maintain, and extend over time.",
                "1. How does the Open/Closed Principle reduce regression bugs?\n2. What code smell signals a violation of the Single Responsibility Principle?",
                List.of(
                        new QuizQuestionCreateRequest(1, "What does the Single Responsibility Principle state?", "A class must have at least two responsibilities", "A class should have only one reason to change", "All methods must be public", "Subclasses must inherit all superclass methods", 1, "SRP states a class should have one reason to change."),
                        new QuizQuestionCreateRequest(2, "Which principle advises high-level modules to depend on interfaces rather than concrete classes?", "Open/Closed Principle", "Interface Segregation", "Dependency Inversion Principle", "Encapsulation", 2, "DIP dictates depending upon abstractions rather than concrete classes.")
                ));

        // Course 3: Java Collections Framework
        Course c3 = saveCourse("java-collections-framework", "Java Collections Framework",
                "Deep dive into Lists, Sets, Maps, Queues, internal hashing mechanisms, performance time complexities, and thread-safe collections.",
                "java-developer", "Java Developer", "Collections", QuestionDifficulty.INTERMEDIATE, "3 hours", "bi-collection", "primary");
        addLesson(c3, 1, "Lists, Sets, Queues & Their Time Complexities", 25,
                "• Master ArrayList vs LinkedList performance\n• Understand HashSet, LinkedHashSet, and TreeSet\n• Utilize PriorityQueue and ArrayDeque",
                "The Java Collections Framework provides standardized data structures and algorithms, eliminating the need to write custom dynamic arrays or linked lists.",
                "### Collections Hierarchy\n\n- `List`: Ordered collection with indexed access. `ArrayList` provides O(1) random access backed by a dynamic array. `LinkedList` is a doubly-linked list with O(1) head/tail insertions.\n- `Set`: Disallows duplicate elements. `HashSet` uses hashing for O(1) lookup. `TreeSet` guarantees O(log n) sorted order.\n- `Queue` / `Deque`: FIFO and double-ended queues. `ArrayDeque` offers superior performance to `Stack` or `LinkedList`.",
                "import java.util.*;\n\npublic class CollectionsDemo {\n    public static void main(String[] args) {\n        // List example\n        List<String> frameworkList = new ArrayList<>(List.of(\"Spring\", \"Hibernate\", \"Quarkus\"));\n        frameworkList.add(\"Micronaut\");\n\n        // Set disallows duplicates\n        Set<String> uniqueSkills = new HashSet<>(frameworkList);\n        uniqueSkills.add(\"Spring\"); // Duplicate ignored\n        System.out.println(\"Unique count: \" + uniqueSkills.size());\n\n        // Queue\n        Queue<String> jobQueue = new ArrayDeque<>();\n        jobQueue.offer(\"Job #1\");\n        jobQueue.offer(\"Job #2\");\n        System.out.println(\"Next job: \" + jobQueue.poll());\n    }\n}",
                "• `ArrayList` expands by 50% capacity when full.\n• Always override `equals()` and `hashCode()` together when storing custom objects in Hash-based collections.",
                "Choose `ArrayList` for fast random lookups, `HashSet` for distinct element tracking, and `ArrayDeque` for efficient queueing.",
                "1. Why is ArrayList faster than LinkedList for random indexed reads?\n2. What is the contract between equals() and hashCode()?",
                List.of(
                        new QuizQuestionCreateRequest(1, "What is the average time complexity of get(index) in an ArrayList?", "O(n)", "O(1)", "O(log n)", "O(n^2)", 1, "ArrayList is backed by an array, allowing O(1) constant-time indexed access."),
                        new QuizQuestionCreateRequest(2, "Which Collection type guarantees that elements are maintained without duplicates in natural sorted order?", "HashSet", "LinkedHashSet", "TreeSet", "ArrayList", 2, "TreeSet uses a red-black tree to store elements in sorted order without duplicates.")
                ));

        addLesson(c3, 2, "HashMaps, TreeMaps & Concurrent Collections", 25,
                "• Deep dive into HashMap bucket arrays and hash collision treeification\n• Understand TreeMap and red-black tree ordering\n• Learn ConcurrentHashMap thread-safe lock striping",
                "Maps associate unique keys with values. Mastering `HashMap` internals is essential for backend engineering and system optimization.",
                "### HashMap Internals\n\n1. Keys compute `hash(key.hashCode())` to find the target bucket index.\n2. In case of collisions, keys are stored in a linked list.\n3. In Java 8+, if a single bucket reaches 8 entries (`TREEIFY_THRESHOLD`) and capacity >= 64, it converts into a Red-Black Tree (`TreeNode`), improving worst-case search from O(n) to O(log n).\n\n### Thread Safety: ConcurrentHashMap\n\n`ConcurrentHashMap` provides high concurrency by synchronizing only the head node of individual buckets rather than locking the entire map like `Hashtable`.",
                "import java.util.concurrent.ConcurrentHashMap;\nimport java.util.*;\n\npublic class MapDemo {\n    public static void main(String[] args) {\n        Map<String, Integer> studentScores = new HashMap<>();\n        studentScores.put(\"Java\", 92);\n        studentScores.put(\"SQL\", 85);\n        studentScores.put(\"Docker\", 78);\n\n        studentScores.forEach((skill, score) -> \n            System.out.printf(\"Skill: %-10s Score: %d%%%n\", skill, score)\n        );\n\n        // Thread-safe map for high-concurrency environments\n        ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();\n        cache.put(\"activeUser\", \"student@skillbridge.ai\");\n    }\n}",
                "• Initial HashMap default capacity is 16 with a load factor of 0.75.\n• Never use standard `HashMap` in multi-threaded contexts without synchronization, as resizing can cause infinite loops or corrupted state.",
                "HashMap provides O(1) expected time for lookup and insertion. ConcurrentHashMap provides thread safety with high throughput using bucket-level locking.",
                "1. What happens when two distinct keys yield identical bucket indexes in a HashMap?\n2. Why is ConcurrentHashMap preferred over Collections.synchronizedMap()?",
                List.of(
                        new QuizQuestionCreateRequest(1, "What data structure does Java 8+ HashMap convert linked list buckets into when collisions exceed 8?", "Circular Array", "Red-Black Balanced Tree", "Doubly-Linked Queue", "Min-Heap", 1, "Java 8 converts colliding buckets with >= 8 entries into Red-Black trees to maintain O(log n) lookup."),
                        new QuizQuestionCreateRequest(2, "How does ConcurrentHashMap achieve high read/write concurrency without global locking?", "By locking the entire table on every write", "By locking only individual bucket head nodes and using lock-free reads", "By executing operations in a single background thread", "By discarding updates during conflicts", 1, "ConcurrentHashMap uses fine-grained bucket-level synchronization and CAS operations.")
                ));

        // Course 4: Exception Handling
        Course c4 = saveCourse("exception-handling", "Exception Handling",
                "Learn checked vs unchecked exceptions, try-with-resources, custom exception hierarchies, and robust production error-handling strategies.",
                "java-developer", "Java Developer", "Exception Handling", QuestionDifficulty.BEGINNER, "2.5 hours", "bi-exclamation-triangle", "primary");
        addLesson(c4, 1, "Checked vs Unchecked Exceptions & Error Handling", 25,
                "• Differentiate between Throwable, Error, Exception, and RuntimeException\n• Implement try-catch-finally blocks properly\n• Master try-with-resources and AutoCloseable",
                "Exceptions represent abnormal conditions that disrupt the normal flow of instruction execution. Proper handling prevents application crashes and resource leaks.",
                "### The Exception Hierarchy\n\n- `Throwable`: Root class for all errors and exceptions.\n- `Error`: Fatal issues like `OutOfMemoryError` or `StackOverflowError` that applications should not attempt to catch.\n- `Exception` (Checked): Compile-time enforced exceptions (e.g., `IOException`, `SQLException`). Must be caught or declared in the method signature with `throws`.\n- `RuntimeException` (Unchecked): Programming defects (e.g., `NullPointerException`, `IllegalArgumentException`). Compiler does not require explicit handling.",
                "import java.io.*;\n\npublic class ExceptionDemo {\n    // Try-with-resources automatically closes AutoCloseable resources\n    public static String readFile(String path) throws IOException {\n        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {\n            return reader.readLine();\n        } catch (FileNotFoundException e) {\n            System.err.println(\"File missing: \" + e.getMessage());\n            throw e; // Re-throw to caller\n        }\n    }\n\n    public static void main(String[] args) {\n        try {\n            readFile(\"config.txt\");\n        } catch (IOException e) {\n            System.out.println(\"Handled IO exception cleanly.\");\n        }\n    }\n}",
                "• Always use try-with-resources for I/O streams and database connections to avoid leaks.\n• Never swallow exceptions with empty `catch (Exception e) {}` blocks.",
                "Checked exceptions enforce recovery for external failures; unchecked exceptions signify logical defects. Try-with-resources guarantees automatic resource cleanup.",
                "1. What is the fundamental difference between Error and Exception in Java?\n2. What interface must an object implement to be used in try-with-resources?",
                List.of(
                        new QuizQuestionCreateRequest(1, "Which exception class hierarchy represents unchecked exceptions that the compiler does not force you to declare?", "java.lang.Exception", "java.lang.RuntimeException", "java.io.IOException", "java.sql.SQLException", 1, "RuntimeException and its subclasses are unchecked exceptions."),
                        new QuizQuestionCreateRequest(2, "What interface must a resource class implement to qualify for use inside a try-with-resources block?", "java.io.Serializable", "java.lang.AutoCloseable", "java.lang.Cloneable", "java.lang.Comparable", 1, "Try-with-resources requires classes that implement AutoCloseable or Closeable.")
                ));

        // Course 5: SQL Fundamentals
        Course c5 = saveCourse("sql-fundamentals", "SQL Fundamentals",
                "Master relational database concepts, SELECT queries, INNER/OUTER JOINs, GROUP BY aggregations, subqueries, and index optimization.",
                "java-developer", "Java Developer", "SQL", QuestionDifficulty.BEGINNER, "3.5 hours", "bi-database", "primary");
        addLesson(c5, 1, "Relational Database Design & Basic SQL Queries", 25,
                "• Understand tables, primary keys, and foreign keys\n• Write SELECT, WHERE, ORDER BY, and LIMIT queries\n• Perform INSERT, UPDATE, and DELETE data manipulation",
                "Structured Query Language (SQL) is the universal language for managing, querying, and updating relational databases like MySQL and PostgreSQL.",
                "### Relational Foundations\n\n- **Primary Key (PK)**: Unique identifier for each row in a table.\n- **Foreign Key (FK)**: Enforces referential integrity by referencing a Primary Key in another table.\n- **ACID Properties**: Atomicity, Consistency, Isolation, and Durability guarantee dependable database transaction processing.",
                "-- Create Table with Primary and Foreign Keys\nCREATE TABLE students (\n    id BIGINT AUTO_INCREMENT PRIMARY KEY,\n    full_name VARCHAR(100) NOT NULL,\n    email VARCHAR(120) UNIQUE NOT NULL,\n    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n);\n\n-- Filtering and Sorting Queries\nSELECT full_name, email\nFROM students\nWHERE email LIKE '%@skillbridge.ai'\nORDER BY full_name ASC\nLIMIT 10;",
                "• Always index columns heavily queried in `WHERE` and `JOIN` clauses.\n• Use parameterized queries/prepared statements in backend code to prevent SQL injection.",
                "Relational models organize data in tables linked by keys. Basic queries filter, sort, and paginate data efficiently.",
                "1. What is the function of a Foreign Key constraint?\n2. Why should SQL queries avoid using SELECT * in production?",
                List.of(
                        new QuizQuestionCreateRequest(1, "Which SQL clause is used to filter records based on specified criteria?", "GROUP BY", "WHERE", "ORDER BY", "HAVING", 1, "The WHERE clause filters rows before aggregation."),
                        new QuizQuestionCreateRequest(2, "What does the 'A' in database ACID properties stand for?", "Accessibility", "Atomicity", "Availability", "Association", 1, "Atomicity ensures that all operations in a transaction succeed together or all rollback.")
                ));

        addLesson(c5, 2, "Joins, Aggregations & Query Optimization", 30,
                "• Master INNER, LEFT, RIGHT, and FULL OUTER JOINs\n• Group data using GROUP BY and HAVING clauses\n• Understand B-Tree indexes and query execution plans",
                "Advanced SQL querying combines rows from multiple related tables and performs summary statistics across large datasets.",
                "### SQL Joins Explained\n\n- **INNER JOIN**: Returns records with matching values in both tables.\n- **LEFT JOIN**: Returns all records from the left table and matched records from the right table.\n- **GROUP BY & HAVING**: Groups rows that share values; `HAVING` filters aggregated groupings, while `WHERE` filters individual rows.",
                "-- Multi-table Join with Aggregation\nSELECT \n    c.title AS career_name,\n    COUNT(a.id) AS total_assessments,\n    ROUND(AVG(a.readiness_percentage), 1) AS avg_readiness\nFROM careers c\nLEFT JOIN career_assessments a ON c.id = a.career_id\nGROUP BY c.id, c.title\nHAVING COUNT(a.id) > 0\nORDER BY avg_readiness DESC;",
                "• `WHERE` filters rows before grouping; `HAVING` filters aggregated values after grouping.\n• Use `EXPLAIN` to verify whether queries use table scans or index lookups.",
                "Joins connect normalized entities, while GROUP BY calculates aggregate analytics. Indexes dramatically accelerate join and filter performance.",
                "1. What is the difference between WHERE and HAVING in SQL?\n2. When should a LEFT JOIN be preferred over an INNER JOIN?",
                List.of(
                        new QuizQuestionCreateRequest(1, "What is the key difference between WHERE and HAVING clauses in SQL?", "WHERE filters after aggregation; HAVING filters before", "WHERE filters individual rows before aggregation; HAVING filters aggregated groups", "They are completely interchangeable", "HAVING can only be used with primary keys", 1, "WHERE filters rows before GROUP BY; HAVING filters grouped aggregate calculations."),
                        new QuizQuestionCreateRequest(2, "Which JOIN returns all records from Table A, along with matching records from Table B, filling unmatched records with NULL?", "INNER JOIN", "LEFT JOIN", "CROSS JOIN", "RIGHT INNER JOIN", 1, "LEFT JOIN keeps all rows from the left table regardless of matching records.")
                ));

        // Course 6: Spring Boot Fundamentals
        Course c6 = saveCourse("spring-boot-fundamentals", "Spring Boot Fundamentals",
                "Build modern RESTful microservices with Spring Boot 3, Dependency Injection, Spring Data JPA, Spring Security, and application configuration.",
                "java-developer", "Java Developer", "Spring Boot", QuestionDifficulty.INTERMEDIATE, "4 hours", "bi-gear-wide-connected", "primary");
        addLesson(c6, 1, "Spring Core: Inversion of Control & Dependency Injection", 25,
                "• Understand Inversion of Control (IoC) and the Spring ApplicationContext\n• Master @Component, @Service, @Repository, and @Controller\n• Use Constructor Injection for clean testability",
                "Spring Boot is the enterprise standard framework for Java applications. At its heart lies the Inversion of Control container, which manages bean lifecycles and dependency injection.",
                "### Inversion of Control & Spring Beans\n\nInstead of classes manually instantiating their dependencies with `new`, Spring's IoC container instantiates, configures, and assembles application objects (Beans).\n\n### Dependency Injection Patterns\n\nConstructor injection is recommended over `@Autowired` field injection because it ensures immutability (`final` fields) and allows simple unit testing without launching a Spring context.",
                "@Service\npublic class StudentService {\n    private final UserRepository userRepository;\n\n    // Recommended: Constructor Injection\n    public StudentService(UserRepository userRepository) {\n        this.userRepository = userRepository;\n    }\n\n    public User findStudent(String email) {\n        return userRepository.findByEmailIgnoreCase(email)\n                .orElseThrow(() -> new RuntimeException(\"Student not found: \" + email));\n    }\n}",
                "• Always use constructor injection for required dependencies.\n• Use `@Configuration` and `@Bean` when configuring third-party libraries that you cannot annotate directly.",
                "Spring's IoC container simplifies component coupling. Constructor injection ensures immutability, cleaner code, and test isolation.",
                "1. Why is constructor injection preferred over field injection in Spring Boot?\n2. What is the role of the Spring ApplicationContext?",
                List.of(
                        new QuizQuestionCreateRequest(1, "Why is constructor injection preferred over field injection (@Autowired on fields)?", "It makes applications boot 10x faster", "It enables final immutable fields and simplifies isolated unit testing without Spring context", "Field injection is deprecated in Java 17", "Constructor injection requires no bytecode compilation", 1, "Constructor injection allows fields to be final and simplifies mock injection during unit tests."),
                        new QuizQuestionCreateRequest(2, "Which annotation marks a class as a business-logic service bean managed by the Spring IoC container?", "@Service", "@Entity", "@Table", "@RequestParam", 0, "@Service specializes @Component to designate service-layer beans.")
                ));

        addLesson(c6, 2, "Building RESTful Web APIs with Spring MVC & JPA", 30,
                "• Implement @RestController with GET, POST, PUT, DELETE mappings\n• Handle DTO validation with Jakarta Validation\n• Manage transactional database operations with Spring Data JPA",
                "Modern distributed web applications exchange data via lightweight JSON REST APIs. Spring Boot makes designing secure, production-grade endpoints intuitive.",
                "### Spring MVC Architecture\n\n- `@RestController`: Combines `@Controller` and `@ResponseBody`, automatically serializing Java objects into JSON.\n- `@GetMapping`, `@PostMapping`, etc.: Map incoming HTTP requests to controller methods.\n- `@Valid`: Enforces bean validation constraints (e.g. `@NotBlank`, `@Size`).\n- Spring Data JPA: Generates CRUD repository queries automatically from interface method names.",
                "@RestController\n@RequestMapping(\"/api/students\")\npublic class StudentController {\n    private final StudentService studentService;\n\n    public StudentController(StudentService studentService) {\n        this.studentService = studentService;\n    }\n\n    @GetMapping(\"/{id}\")\n    public ResponseEntity<UserResponse> getStudent(@PathVariable Long id) {\n        User user = studentService.findById(id);\n        return ResponseEntity.ok(new UserResponse(user.getId(), user.getFullName(), user.getEmail()));\n    }\n}",
                "• Never expose raw JPA entities directly to the client; always use DTOs to prevent unwanted data leakage and circular serialization.\n• Use `@Transactional` on service methods to guarantee atomic database commits or rollbacks on failure.",
                "Spring Boot's REST controllers serialize data seamlessly to JSON, while Spring Data JPA eliminates boilerplate persistence logic.",
                "1. Why should APIs return DTOs rather than raw JPA entities?\n2. How does Spring Boot auto-configuration discover dependencies?",
                List.of(
                        new QuizQuestionCreateRequest(1, "Which Spring annotation combines @Controller and @ResponseBody to return JSON directly from endpoint methods?", "@RestController", "@WebEndpoint", "@Service", "@Repository", 0, "@RestController signals that return values should be serialized directly into HTTP response bodies."),
                        new QuizQuestionCreateRequest(2, "What is the primary benefit of using Data Transfer Objects (DTOs) for API request and response bodies?", "It speeds up network cable transfer", "It prevents over-exposing internal database schema details and avoids circular entity reference errors", "DTOs are required by the HTTP standard", "It replaces the need for a database", 1, "DTOs decouple public API contracts from internal entity structures.")
                ));
    }

    // =========================================================================
    // 2. FULL STACK DEVELOPER COURSES (6 Courses)
    // =========================================================================
    private void seedFullStackDeveloperCourses() {
        // Course 7: HTML Fundamentals
        Course c7 = saveCourse("html-fundamentals", "HTML Fundamentals",
                "Learn semantic HTML5 elements, DOM structure, forms, validation, accessibility (a11y), and web page architecture.",
                "full-stack-developer", "Full Stack Developer", "HTML", QuestionDifficulty.BEGINNER, "2 hours", "bi-filetype-html", "info");
        addLesson(c7, 1, "Semantic HTML5 and Modern Web Structure", 20,
                "• Master the DOM tree hierarchy\n• Use semantic tags: header, nav, main, section, article, footer\n• Ensure accessibility with ARIA attributes and alt tags",
                "HyperText Markup Language (HTML) is the skeleton of all web content. Semantic HTML gives meaning to page structures, enhancing accessibility and search engine optimization (SEO).",
                "### Semantic Elements vs Div Soup\n\nTraditional web pages relied heavily on generic `<div>` tags. HTML5 introduced semantic elements:\n\n- `<header>`: Introduces page or section banner content.\n- `<nav>`: Encloses navigation links.\n- `<main>`: Contains the dominant, unique page content.\n- `<article>`: Self-contained, syndicatable content.\n- `<section>`: Thematic grouping of content.\n- `<footer>`: Closing metadata, copyright, and contact information.",
                "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n    <meta charset=\"UTF-8\">\n    <title>Accessible Web Page</title>\n</head>\n<body>\n    <header>\n        <h1>SkillBridge AI</h1>\n        <nav aria-label=\"Primary Navigation\">\n            <ul>\n                <li><a href=\"#courses\">Courses</a></li>\n                <li><a href=\"#roadmap\">Roadmap</a></li>\n            </ul>\n        </nav>\n    </header>\n    <main>\n        <article>\n            <h2>Getting Started with Web Development</h2>\n            <p>Semantic tags improve accessibility for screen readers.</p>\n        </article>\n    </main>\n    <footer>\n        <p>&copy; 2026 SkillBridge AI</p>\n    </footer>\n</body>\n</html>",
                "• Always provide descriptive `alt` text on images for screen readers.\n• Always specify `<html lang=\"en\">` for accessibility compliance.",
                "Semantic HTML provides meaningful structure, improves search indexing, and ensures universal accessibility for assistive technologies.",
                "1. Why is semantic markup superior to generic <div> tags?\n2. What is the purpose of the <main> element?",
                List.of(
                        new QuizQuestionCreateRequest(1, "Which HTML5 semantic element is intended to contain the central, unique content of a document?", "<section>", "<main>", "<div>", "<aside>", 1, "The <main> element represents the dominant content unique to the document."),
                        new QuizQuestionCreateRequest(2, "Why should images always have an 'alt' attribute?", "It is required for image compression", "It provides screen readers and text browsers with context when images fail to load", "It sets the CSS styling", "It defines image width and height", 1, "The alt attribute ensures web accessibility for vision-impaired users using screen readers.")
                ));

        // Course 8: CSS and Responsive Design
        Course c8 = saveCourse("css-and-responsive-design", "CSS and Responsive Design",
                "Master CSS Box Model, Flexbox, CSS Grid, media queries, mobile-first design, and modern responsive UI styling.",
                "full-stack-developer", "Full Stack Developer", "CSS", QuestionDifficulty.BEGINNER, "3 hours", "bi-filetype-css", "info");
        addLesson(c8, 1, "The Box Model, Flexbox, and Responsive Layouts", 25,
                "• Understand Content, Padding, Border, and Margin\n• Master Flexbox axes, alignment, and wrapping\n• Implement mobile-first media queries",
                "Cascading Style Sheets (CSS) controls the visual presentation and layout of web documents. Modern layouts depend on Flexbox and Grid to look pristine across smartphones, tablets, and desktop displays.",
                "### The Box Model\n\nEvery HTML element is rendered as a rectangular box comprising:\n1. **Content**: Text and media.\n2. **Padding**: Transparent space inside the border.\n3. **Border**: Outline surrounding padding.\n4. **Margin**: Space outside the border separating elements.\n\n`box-sizing: border-box` makes sizing intuitive by including padding and border within the declared width.",
                "/* Modern Responsive Box Sizing */\n* {\n    box-sizing: border-box;\n    margin: 0;\n    padding: 0;\n}\n\n/* Flexbox Responsive Container */\n.card-container {\n    display: flex;\n    flex-direction: row;\n    flex-wrap: wrap;\n    gap: 1.5rem;\n    justify-content: center;\n}\n\n/* Mobile-First Media Query */\n@media (max-width: 768px) {\n    .card-container {\n        flex-direction: column;\n        align-items: stretch;\n    }\n}",
                "• Always use `box-sizing: border-box` globally to avoid layout overflow bugs.\n• Design mobile-first using `min-width` queries to deliver clean responsive experiences.",
                "The Box Model governs element sizing. Flexbox manages 1-dimensional component alignment, and media queries adapt layouts to varied viewport widths.",
                "1. What does box-sizing: border-box do?\n2. What is the difference between justify-content and align-items in Flexbox?",
                List.of(
                        new QuizQuestionCreateRequest(1, "What does setting 'box-sizing: border-box' accomplish in CSS?", "It adds a default shadow around all borders", "It includes padding and borders in the element's total specified width and height", "It hides element margins", "It converts the element to an inline block", 1, "border-box ensures padding and borders do not expand the element beyond its defined width/height."),
                        new QuizQuestionCreateRequest(2, "In CSS Flexbox with row direction, which property aligns flex items along the main horizontal axis?", "align-items", "justify-content", "flex-wrap", "align-content", 1, "justify-content aligns items along the primary axis.")
                ));

        // Course 9: JavaScript Fundamentals
        Course c9 = saveCourse("javascript-fundamentals", "JavaScript Fundamentals",
                "Learn ES6+ syntax, asynchronous programming with Promises and async/await, DOM manipulation, closures, and modern web browser events.",
                "full-stack-developer", "Full Stack Developer", "JavaScript", QuestionDifficulty.BEGINNER, "3.5 hours", "bi-filetype-js", "info");
        addLesson(c9, 1, "ES6+ Syntax, Scoping & Async/Await", 30,
                "• Master let, const, and block scoping\n• Understand arrow functions and destructuring\n• Handle asynchronous HTTP calls using Fetch and async/await",
                "JavaScript brings interactivity to the web browser. Modern JavaScript (ES6+) provides expressive syntax and non-blocking asynchronous primitives.",
                "### Modern JavaScript Fundamentals\n\n- `const` & `let`: Block-scoped variable declarations replacing legacy `var`.\n- Arrow functions (`() => {}`): Concise syntax with lexical `this` binding.\n- Destructuring: Clean unpacking of properties from objects and arrays.\n- Asynchronous execution: Promises and `async/await` handle non-blocking operations like network requests gracefully.",
                "// Asynchronous API call using modern Async/Await\nasync function fetchStudentRoadmap() {\n    try {\n        const response = await fetch('/api/courses');\n        if (!response.ok) {\n            throw new Error(`HTTP error! status: ${response.status}`);\n        }\n        const courses = await response.json();\n        console.log(`Loaded ${courses.length} courses:`, courses);\n        return courses;\n    } catch (error) {\n        console.error('Failed to load courses:', error);\n        return [];\n    }\n}\n\n// Array destructuring and map\nconst [firstCourse, ...rest] = ['Java 17', 'SQL', 'Spring Boot'];",
                "• Never use `var` in modern JavaScript; use `const` by default and `let` only for variables that will be reassigned.\n• Always handle errors in asynchronous code using `try...catch` with `async/await`.",
                "Modern JS uses block scoping and async/await for clear, readable asynchronous workflows without callback hell.",
                "1. Why is const preferred over let and var?\n2. How does async/await improve code readability over Promise .then() chains?",
                List.of(
                        new QuizQuestionCreateRequest(1, "What type of scoping do 'let' and 'const' variables have in JavaScript?", "Function scoping", "Global scoping only", "Block scoping", "No scoping restrictions", 2, "let and const are scoped to the nearest enclosing block (`{}`)."),
                        new QuizQuestionCreateRequest(2, "What does an 'async' function in JavaScript always return?", "A String", "A Promise", "A Callback function", "Undefined", 1, "Functions declared with 'async' always wrap their return value in a Promise.")
                ));

        // Course 10: REST API Development
        Course c10 = saveCourse("rest-api-development", "REST API Development",
                "Understand RESTful architectural principles, HTTP methods, status codes, stateless authentication, JSON serialization, and API versioning.",
                "full-stack-developer", "Full Stack Developer", "REST APIs", QuestionDifficulty.INTERMEDIATE, "3 hours", "bi-router", "info");
        addLesson(c10, 1, "REST Constraints, HTTP Verbs & Status Codes", 25,
                "• Understand Client-Server and Statelessness constraints\n• Master GET, POST, PUT, PATCH, and DELETE verbs\n• Learn proper HTTP status codes: 200, 201, 400, 401, 403, 404, 500",
                "Representational State Transfer (REST) is an architectural style that leverages HTTP for distributed communication between decoupled frontends and backend servers.",
                "### Core REST Constraints\n\n1. **Stateless**: Each request must contain all information necessary to understand and process the request.\n2. **Uniform Interface**: Resources are identified by standard URIs (e.g. `/api/courses`).\n\n### HTTP Methods & Status Codes\n\n- `GET`: Retrieve resource (safe and idempotent).\n- `POST`: Create resource (201 Created).\n- `PUT`: Complete resource replacement (idempotent).\n- `PATCH`: Partial resource update.\n- `DELETE`: Remove resource (204 No Content).",
                "// Typical REST Resource Endpoints\n// GET    /api/courses          -> List courses (200 OK)\n// POST   /api/courses          -> Create new course (201 Created)\n// GET    /api/courses/{id}     -> Fetch single course (200 OK or 404 Not Found)\n// PUT    /api/courses/{id}     -> Replace course attributes (200 OK)\n// DELETE /api/courses/{id}     -> Remove course (204 No Content)",
                "• URIs should represent resources (nouns like `/api/courses`), not operations (verbs like `/api/getCourses`).\n• Always return appropriate status codes (401 Unauthorized for missing token, 403 Forbidden for insufficient permissions).",
                "RESTful design relies on statelessness, standard nouns in URIs, and accurate HTTP status codes to communicate client-server intent.",
                "1. What is the difference between 401 Unauthorized and 403 Forbidden?\n2. What does idempotency mean in the context of HTTP methods?",
                List.of(
                        new QuizQuestionCreateRequest(1, "Which HTTP status code should be returned when a new resource is successfully created?", "200 OK", "201 Created", "204 No Content", "302 Found", 1, "HTTP 201 Created explicitly confirms new resource creation."),
                        new QuizQuestionCreateRequest(2, "What is the key difference between PUT and PATCH in RESTful design?", "PUT is for deleting; PATCH is for updating", "PUT replaces the entire resource; PATCH applies partial modifications", "PATCH is idempotent, while PUT is not", "There is no difference", 1, "PUT performs full resource replacement; PATCH applies partial modifications.")
                ));

        // Course 11: Backend Development
        Course c11 = saveCourse("backend-development", "Backend Development",
                "Learn server-side application architecture, layered services, authentication with JWT, middleware, and request lifecycles.",
                "full-stack-developer", "Full Stack Developer", "Backend development", QuestionDifficulty.INTERMEDIATE, "3.5 hours", "bi-hdd-network", "info");
        addLesson(c11, 1, "Layered Architecture, Middleware & JWT Security", 30,
                "• Understand Controller-Service-Repository separation\n• Master JSON Web Tokens (JWT) structure: Header, Payload, Signature\n• Implement stateless request filter pipelines",
                "Backend services handle business logic, enforce authorization policies, and orchestrate persistence layers cleanly.",
                "### Three-Tier Layered Architecture\n\n1. **Web / Controller Layer**: Handles HTTP protocol concerns, deserializes payloads, and delegates.\n2. **Business / Service Layer**: Contains core domain logic, validation rules, and transactional boundaries.\n3. **Persistence / Repository Layer**: Interfaces directly with the database.\n\n### JSON Web Tokens (JWT)\n\nJWT enables stateless authentication. Tokens contain three base64-encoded segments: `Header.Payload.Signature`. The server verifies authenticity by computing the HMAC-SHA256 signature using a secret key.",
                "// JWT Bearer Authentication Filter Flow\npublic class AuthFilterExample {\n    // 1. Client sends: 'Authorization: Bearer <jwt-token>'\n    // 2. Server extracts token\n    // 3. Server validates signature against secret\n    // 4. Server extracts username/roles and populates SecurityContext\n    // 5. Request proceeds to protected Controller endpoint\n}",
                "• Never store sensitive data like raw passwords in a JWT payload; payloads are base64-encoded and readable by anyone.\n• Use strong secrets (at least 256 bits) for HMAC-SHA256 signatures.",
                "Layered architecture keeps concerns separated and testable. JWT allows horizontal scaling without session-affinity bottlenecks.",
                "1. Why are JWTs suitable for horizontally scaled microservices?\n2. What are the three parts of a JSON Web Token?",
                List.of(
                        new QuizQuestionCreateRequest(1, "What are the three dot-separated components of a JSON Web Token (JWT)?", "Protocol, Data, Checksum", "Header, Payload, Signature", "Username, Password, Hash", "Issuer, Audience, Expiry", 1, "A JWT consists of Header, Payload, and cryptographic Signature."),
                        new QuizQuestionCreateRequest(2, "Why does a layered architecture keep Controllers separate from Repositories?", "To increase file count", "To isolate business logic and ensure database operations can be swapped or tested independently", "Because Java forces all classes into separate folders", "To improve Wi-Fi signal", 1, "Layering decouples web concerns from persistence concerns and business rules.")
                ));

        // Course 12: Database Fundamentals
        Course c12 = saveCourse("database-fundamentals", "Database Fundamentals",
                "Understand data modeling, database normalization (1NF, 2NF, 3NF), transactions, concurrency isolation, and indexes.",
                "full-stack-developer", "Full Stack Developer", "Database fundamentals", QuestionDifficulty.BEGINNER, "3 hours", "bi-database-fill-gear", "info");
        addLesson(c12, 1, "Database Normalization and Indexing Principles", 25,
                "• Understand First, Second, and Third Normal Forms (1NF, 2NF, 3NF)\n• Prevent data redundancy and update anomalies\n• Understand how B-Tree indexes speed up lookups",
                "Databases store the enduring state of an enterprise. Proper schema normalization prevents duplicate data and structural anomalies.",
                "### Normal Forms\n\n- **1NF**: Eliminate duplicate columns and ensure atomic, non-decomposable cell values.\n- **2NF**: Meet 1NF and ensure all non-key attributes are fully functionally dependent on the entire primary key.\n- **3NF**: Meet 2NF and eliminate transitive dependencies (non-key attributes depending on other non-key attributes).\n\n### B-Tree Indexes\n\nIndexes maintain balanced search trees allowing logarithmic O(log n) lookups instead of full table scans O(n).",
                "-- 3NF Compliant Normalized Tables\nCREATE TABLE departments (\n    id INT AUTO_INCREMENT PRIMARY KEY,\n    name VARCHAR(80) NOT NULL\n);\n\nCREATE TABLE employees (\n    id INT AUTO_INCREMENT PRIMARY KEY,\n    name VARCHAR(100) NOT NULL,\n    department_id INT NOT NULL,\n    FOREIGN KEY (department_id) REFERENCES departments(id)\n);\n\n-- Create Index for High-Performance Queries\nCREATE INDEX idx_emp_dept ON employees(department_id);",
                "• Over-indexing slows down INSERT, UPDATE, and DELETE operations because indexes must be updated on every write.\n• Index columns that have high selectivity (many distinct values).",
                "Normalization eliminates data redundancy; indexes accelerate query execution at the cost of slight write overhead.",
                "1. What is a transitive dependency in database design?\n2. What is the trade-off of adding too many indexes to a table?",
                List.of(
                        new QuizQuestionCreateRequest(1, "Which normal form requires eliminating transitive dependencies between non-primary key columns?", "First Normal Form (1NF)", "Second Normal Form (2NF)", "Third Normal Form (3NF)", "Boyce-Codd Normal Form (BCNF)", 2, "3NF eliminates transitive dependencies between non-key attributes."),
                        new QuizQuestionCreateRequest(2, "What is the primary downside of adding excessive indexes to a database table?", "Queries become completely disabled", "Write operations (INSERT, UPDATE, DELETE) become slower", "The database stops accepting foreign keys", "Disk storage becomes corrupt", 1, "Each index must be recalculated and written during data modifications.")
                ));
    }

    // =========================================================================
    // 3. DATA SCIENTIST COURSES (5 Courses)
    // =========================================================================
    private void seedDataScientistCourses() {
        // Course 13: Python Programming
        Course c13 = saveCourse("python-programming", "Python Programming",
                "Master Python fundamentals, lists, dictionaries, list comprehensions, generators, functions, and object-oriented Python.",
                "data-scientist", "Data Scientist", "Python Fundamentals", QuestionDifficulty.BEGINNER, "3 hours", "bi-filetype-py", "success");
        addLesson(c13, 1, "Python Data Structures, Comprehensions & Functions", 25,
                "• Master lists, tuples, sets, and dictionaries\n• Write elegant list and dictionary comprehensions\n• Use lambda functions, args, and kwargs",
                "Python is the preeminent programming language for Data Science and Machine Learning due to its readability, rich library ecosystem, and versatility.",
                "### Core Python Data Structures\n\n- `list`: Mutable, ordered sequence (`[1, 2, 3]`).\n- `tuple`: Immutable, ordered sequence (`(1, 2, 3)`).\n- `dict`: Key-value hash map (`{'name': 'Ada', 'score': 95}`).\n- `set`: Unordered collection of unique items (`{1, 2, 3}`).",
                "# List and Dict Comprehensions\nnumbers = [1, 2, 3, 4, 5, 6]\neven_squares = [x**2 for x in numbers if x % 2 == 0]\nprint(\"Even squares:\", even_squares)\n\n# Dictionary mapping student to score\nstudents = [('Alice', 88), ('Bob', 94), ('Charlie', 76)]\nhigh_performers = {name: score for name, score in students if score >= 80}\nprint(\"High performers:\", high_performers)",
                "• Tuples are immutable and hashable, making them suitable as dictionary keys; lists are not.\n• Use list comprehensions instead of manual loops for cleaner, faster operations.",
                "Python's native collections and expressive comprehensions enable rapid data exploration and clean transformation scripts.",
                "1. What is the fundamental difference between a Python list and a tuple?\n2. What is a list comprehension?",
                List.of(
                        new QuizQuestionCreateRequest(1, "Which of the following Python data structures is immutable?", "list", "dict", "tuple", "set", 2, "Tuples cannot be altered once created."),
                        new QuizQuestionCreateRequest(2, "What is the output of `[x * 2 for x in [1, 2, 3]]` in Python?", "[1, 2, 3]", "[2, 4, 6]", "[1, 4, 9]", "[2, 2, 2]", 1, "The list comprehension multiplies each element by 2, yielding [2, 4, 6].")
                ));

        // Course 14: Statistics and Probability
        Course c14 = saveCourse("statistics-and-probability", "Statistics and Probability",
                "Learn descriptive statistics, normal distributions, hypothesis testing, p-values, Bayes' Theorem, and variance analysis.",
                "data-scientist", "Data Scientist", "Statistics & Probability", QuestionDifficulty.BEGINNER, "3.5 hours", "bi-graph-up", "success");
        addLesson(c14, 1, "Descriptive Statistics, Distributions & Hypothesis Testing", 30,
                "• Calculate mean, median, mode, variance, and standard deviation\n• Understand normal (Gaussian) distributions and z-scores\n• Formulate null hypotheses and interpret p-values",
                "Statistics is the mathematical foundation of data analysis and predictive modeling, allowing practitioners to separate genuine patterns from random noise.",
                "### Key Concepts\n\n- **Central Tendency**: Mean (average), Median (middle value; robust against outliers), Mode (most frequent).\n- **Dispersion**: Variance measures average squared deviation from the mean; Standard Deviation is its square root.\n- **Normal Distribution**: 68% of data falls within 1 standard deviation, 95% within 2, and 99.7% within 3 (empirical rule).\n- **P-Value**: The probability of observing results at least as extreme as the sample data under the assumption that the null hypothesis ($H_0$) is true. A threshold of p < 0.05 is typically used to reject $H_0$.",
                "import numpy as np\n\ndata = [22, 24, 25, 25, 26, 28, 29, 31, 35, 105] # 105 is an outlier\nmean = np.mean(data)\nmedian = np.median(data)\nstd_dev = np.std(data)\n\nprint(f\"Mean: {mean:.1f}\")     # Heavily skewed by outlier\nprint(f\"Median: {median:.1f}\") # Robust against outlier\nprint(f\"Std Dev: {std_dev:.1f}\")",
                "• When data has severe outliers, prefer the median over the mean.\n• A small p-value does not measure the magnitude or practical importance of an effect.",
                "Descriptive statistics summarize data central tendency and spread. Hypothesis testing with p-values assesses statistical significance.",
                "1. Why is the median more robust than the mean in the presence of extreme outliers?\n2. What does a p-value of 0.03 indicate when alpha = 0.05?",
                List.of(
                        new QuizQuestionCreateRequest(1, "Which metric of central tendency is least affected by extreme numerical outliers?", "Mean", "Median", "Standard Deviation", "Variance", 1, "The median uses rank order and is not distorted by extreme values."),
                        new QuizQuestionCreateRequest(2, "In hypothesis testing, what conclusion is typically drawn when the calculated p-value is less than alpha (e.g. p < 0.05)?", "Accept the null hypothesis as proven true", "Reject the null hypothesis in favor of the alternative hypothesis", "Discard the experiment data", "Increase the sample size", 1, "A p-value below alpha provides sufficient evidence to reject the null hypothesis.")
                ));

        // Course 15: Data Analysis
        Course c15 = saveCourse("data-analysis", "Data Analysis",
                "Learn exploratory data analysis (EDA), data cleaning, handling missing values, feature engineering, and statistical insights extraction.",
                "data-scientist", "Data Scientist", "Data Analysis & Pandas", QuestionDifficulty.BEGINNER, "3 hours", "bi-search", "success");
        addLesson(c15, 1, "Exploratory Data Analysis (EDA) & Data Cleaning", 25,
                "• Identify and handle missing or null data values\n• Detect outliers using Interquartile Range (IQR)\n• Perform univariate and bivariate analysis",
                "Real-world data is messy, noisy, and incomplete. Exploratory Data Analysis (EDA) is the iterative process of inspecting, cleaning, and transforming raw records into actionable analytical datasets.",
                "### Cleaning Pipeline\n\n1. **Handling Missing Values**: Drop rows/columns or impute using mean, median, mode, or k-NN algorithms.\n2. **Outlier Detection**: Interquartile Range ($IQR = Q3 - Q1$). Values beyond $Q1 - 1.5 \\times IQR$ or $Q3 + 1.5 \\times IQR$ represent potential anomalies.\n3. **Feature Scaling**: Normalization (Min-Max) and Standardization (Z-score).",
                "# Data Cleaning Concept Walkthrough\n# 1. Identify missing entries\n# df.isnull().sum()\n# 2. Impute missing numerical values with median\n# df['age'] = df['age'].fillna(df['age'].median())\n# 3. Filter outliers using IQR bounds",
                "• Never impute missing test-set data using parameters calculated from the entire dataset to prevent data leakage.\n• Document every transformation step to ensure reproducibility.",
                "EDA reveals distributions, relationships, and anomalies in raw datasets before modeling.",
                "1. What is the difference between data imputation and deletion?\n2. How does data leakage occur during feature preprocessing?",
                List.of(
                        new QuizQuestionCreateRequest(1, "What formula defines the Interquartile Range (IQR) used in outlier detection?", "Mean - Median", "Q3 - Q1", "Maximum - Minimum", "Variance / Standard Deviation", 1, "IQR is calculated as Q3 minus Q1."),
                        new QuizQuestionCreateRequest(2, "What risk arises when preprocessing and scaling features using the full dataset before splitting into train/test sets?", "Overfitting", "Data Leakage", "Syntax errors", "High bias", 1, "Data leakage occurs when test set distribution characteristics contaminate training preprocessing.")
                ));

        // Course 16: NumPy and Pandas
        Course c16 = saveCourse("numpy-and-pandas", "NumPy and Pandas",
                "Master fast vectorized array computing with NumPy and high-performance tabular data manipulation with Pandas DataFrames.",
                "data-scientist", "Data Scientist", "Data Analysis & Pandas", QuestionDifficulty.INTERMEDIATE, "3.5 hours", "bi-table", "success");
        addLesson(c16, 1, "NumPy Arrays and Pandas DataFrames in Practice", 30,
                "• Master NumPy vectorized operations and broadcasting\n• Filter, group, and merge DataFrames with Pandas\n• Perform fast column transformations with apply and map",
                "NumPy and Pandas are the twin pillars of data science in Python. NumPy executes high-speed numerical C-level array routines, while Pandas delivers flexible tabular data structures.",
                "### NumPy Vectorization vs Python Loops\n\nVectorized operations operate on entire arrays at once without explicit Python `for` loops, yielding 50x-100x speedups thanks to contiguous memory layout and SIMD CPU vector extensions.\n\n### Essential Pandas Operations\n\n- `df.groupby('category').agg({'sales': 'sum'})`: Summary group statistics.\n- `df.merge(other, on='id', how='left')`: Relational join.",
                "import numpy as np\nimport pandas as pd\n\n# Vectorized NumPy Operations\narr = np.array([10, 20, 30, 40])\nscaled = arr * 1.5 + 5 # Fast element-wise vector operation\n\n# Pandas DataFrame creation and aggregation\ndf = pd.DataFrame({\n    'student': ['Alice', 'Bob', 'Charlie', 'Diana'],\n    'career': ['Java', 'Cloud', 'Java', 'AI'],\n    'score': [85, 78, 92, 88]\n})\navg_scores = df.groupby('career')['score'].mean()\nprint(avg_scores)",
                "• Avoid iterating through Pandas rows with `for index, row in df.iterrows()`; use vectorized expressions or `.apply()`.\n• Use `.loc[row, col]` for label indexing and `.iloc[row, col]` for integer position indexing.",
                "NumPy provides fast C-speed numerical arrays; Pandas offers robust tabular manipulation, filtering, and aggregation tools.",
                "1. Why is vectorization in NumPy significantly faster than a Python for loop?\n2. What is the difference between .loc and .iloc in Pandas?",
                List.of(
                        new QuizQuestionCreateRequest(1, "Why are NumPy vectorized array computations dramatically faster than standard Python loops?", "NumPy runs in the browser", "NumPy executes compiled C code over contiguous memory blocks without Python interpreter loop overhead", "NumPy uses threading exclusively", "NumPy runs on external servers", 1, "Vectorized arrays run compiled C routines over contiguous memory structures."),
                        new QuizQuestionCreateRequest(2, "Which Pandas method is used to combine two DataFrames based on a shared key column (similar to a SQL JOIN)?", "df.concat()", "df.merge()", "df.append()", "df.stack()", 1, "df.merge() performs relational join operations between DataFrames.")
                ));

        // Course 17: Machine Learning Fundamentals
        Course c17 = saveCourse("machine-learning-fundamentals", "Machine Learning Fundamentals",
                "Understand supervised vs unsupervised learning, regression, classification, decision trees, model evaluation metrics, and cross-validation.",
                "data-scientist", "Data Scientist", "Machine Learning", QuestionDifficulty.INTERMEDIATE, "4 hours", "bi-cpu", "success");
        addLesson(c17, 1, "Supervised Learning, Model Evaluation & Overfitting", 30,
                "• Differentiate between classification and regression\n• Understand bias-variance tradeoff and overfitting\n• Evaluate models using Precision, Recall, F1-Score, and ROC-AUC",
                "Machine Learning trains algorithms to learn patterns from data and make predictions without being explicitly programmed with deterministic rules.",
                "### Supervised Learning Paradigms\n\n- **Regression**: Predicts continuous numerical values (e.g. house prices, assessment score percentage).\n- **Classification**: Predicts discrete class labels (e.g. spam/not-spam, beginner/intermediate).\n\n### Model Evaluation Metrics\n\nAccuracy can be misleading on imbalanced datasets. Better metrics include:\n- **Precision**: Of all predicted positives, how many are truly positive? ($TP / (TP + FP)$)\n- **Recall**: Of all actual positives, how many did we catch? ($TP / (TP + FN)$)\n- **F1-Score**: Harmonic mean of Precision and Recall.",
                "from sklearn.model_selection import train_test_split\nfrom sklearn.metrics import classification_report, accuracy_score\n# Typical Scikit-Learn training workflow:\n# X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)\n# model.fit(X_train, y_train)\n# predictions = model.predict(X_test)\n# print(classification_report(y_test, predictions))",
                "• Use k-fold cross-validation to get an accurate estimate of model generalization performance.\n• Regularization (L1 Lasso, L2 Ridge) penalizes excessive model complexity to prevent overfitting.",
                "Supervised models predict continuous values or discrete classes. The bias-variance tradeoff guides tuning to achieve high generalization.",
                "1. What is the difference between Precision and Recall?\n2. What causes a machine learning model to overfit?",
                List.of(
                        new QuizQuestionCreateRequest(1, "What metric measures the proportion of actual positive cases that were correctly identified by a model?", "Accuracy", "Recall", "Precision", "Specificity", 1, "Recall (True Positive Rate) measures TP / (TP + FN)."),
                        new QuizQuestionCreateRequest(2, "What is a primary characteristic of an overfitted machine learning model?", "High training error and high test error", "Extremely low training error but poor generalization on unseen test data", "Under-utilization of features", "Slow execution speed", 1, "Overfitted models memorize noise in the training set and fail to generalize.")
                ));
    }

    // =========================================================================
    // 4. CLOUD ENGINEER COURSES (5 Courses)
    // =========================================================================
    private void seedCloudEngineerCourses() {
        // Course 18: Cloud Computing Fundamentals
        Course c18 = saveCourse("cloud-computing-fundamentals", "Cloud Computing Fundamentals",
                "Learn cloud service models (IaaS, PaaS, SaaS), deployment models (Public, Private, Hybrid), high availability, scalability, and cloud economics.",
                "cloud-engineer", "Cloud Engineer", "Cloud fundamentals", QuestionDifficulty.BEGINNER, "2.5 hours", "bi-cloud", "warning");
        addLesson(c18, 1, "Cloud Architectures, Service Models & Core Benefits", 25,
                "• Master IaaS, PaaS, and SaaS distinctions\n• Understand horizontal vs vertical scaling\n• Learn High Availability (HA) and Disaster Recovery (DR)",
                "Cloud computing provides on-demand delivery of compute power, database storage, and IT resources via the internet with pay-as-you-go pricing.",
                "### Cloud Service Models\n\n- **IaaS (Infrastructure as a Service)**: Fundamental compute, storage, and networking (e.g. AWS EC2, GCP Compute Engine). You manage OS and runtimes.\n- **PaaS (Platform as a Service)**: Hardware and software tools provided over the internet (e.g. AWS Elastic Beanstalk, Heroku). You manage application code only.\n- **SaaS (Software as a Service)**: Complete software applications delivered to end users (e.g. Google Workspace, Microsoft 365).\n\n### Scalability: Vertical vs Horizontal\n\n- **Vertical Scaling (Scale-Up)**: Adding more CPU/RAM to an existing instance.\n- **Horizontal Scaling (Scale-Out)**: Adding more instances in parallel behind a load balancer.",
                "# Infrastructure-as-a-Service vs Platform-as-a-Service Comparison\n# IaaS: You provision VM, configure Linux, install Java 17, manage updates\n# PaaS: You push code or container; platform handles OS patches and scaling",
                "• Design for failure: distribute workloads across multiple Availability Zones (AZs).\n• Horizontal scaling provides superior resiliency compared to vertical scaling.",
                "Cloud computing replaces capital expenditure with operational agility through IaaS, PaaS, and SaaS models.",
                "1. What is the fundamental difference between IaaS and PaaS?\n2. What is the difference between horizontal and vertical scaling?",
                List.of(
                        new QuizQuestionCreateRequest(1, "Which cloud service model provides virtual machines where the customer manages the operating system, runtimes, and applications?", "SaaS", "PaaS", "IaaS", "FaaS", 2, "IaaS gives users control over the OS, storage, and deployed applications."),
                        new QuizQuestionCreateRequest(2, "What is horizontal scaling (scale-out) in cloud infrastructure?", "Upgrading a single server with more RAM and CPU", "Adding additional instances in parallel behind a load balancer", "Switching from MySQL to PostgreSQL", "Moving servers to another geographic region", 1, "Horizontal scaling adds more instances to distribute incoming workload traffic.")
                ));

        // Course 19: AWS Fundamentals
        Course c19 = saveCourse("aws-fundamentals", "AWS Fundamentals",
                "Explore Amazon Web Services core infrastructure: EC2, S3, IAM, VPC, RDS, and CloudWatch for production deployments.",
                "cloud-engineer", "Cloud Engineer", "AWS", QuestionDifficulty.BEGINNER, "3.5 hours", "bi-clouds", "warning");
        addLesson(c19, 1, "Core AWS Services: IAM, EC2, S3, and RDS", 30,
                "• Implement Identity and Access Management (IAM) least privilege\n• Launch and configure EC2 compute instances and Security Groups\n• Store static assets in Amazon S3 buckets and configure RDS databases",
                "Amazon Web Services (AWS) is the world's most comprehensive and broadly adopted cloud platform, powering enterprises from startups to Fortune 500s.",
                "### Essential AWS Building Blocks\n\n- **IAM**: Manages identities, roles, and fine-grained permissions. Principle of least privilege is mandatory.\n- **EC2 (Elastic Compute Cloud)**: Virtual servers in the cloud with customizable security groups.\n- **S3 (Simple Storage Service)**: Scalable object storage with 99.999999999% (11 9's) durability.\n- **RDS (Relational Database Service)**: Managed relational databases (MySQL, PostgreSQL) handling backups, replication, and failover automatically.",
                "# Example AWS CLI S3 and IAM Operations\n# aws s3 mb s3://my-skillbridge-bucket\n# aws s3 cp application.jar s3://my-skillbridge-bucket/\n# aws ec2 describe-instances --filters \"Name=instance-state-name,Values=running\"",
                "• Never use the AWS root account for daily engineering tasks; create IAM users with specific policies.\n• Restrict EC2 Security Group inbound ports (e.g. never open SSH port 22 to 0.0.0.0/0).",
                "IAM controls access, EC2 provides scalable compute, S3 stores durable objects, and RDS delivers managed relational databases.",
                "1. What is the Principle of Least Privilege in IAM?\n2. What is an AWS Security Group?",
                List.of(
                        new QuizQuestionCreateRequest(1, "Which AWS service is used to manage users, groups, and permissions to access cloud resources securely?", "Amazon EC2", "AWS IAM", "Amazon CloudWatch", "AWS S3", 1, "IAM (Identity and Access Management) controls authentication and authorization in AWS."),
                        new QuizQuestionCreateRequest(2, "What acts as a virtual firewall for your EC2 instances to control inbound and outbound traffic?", "Security Group", "IAM Role", "S3 Bucket Policy", "Route 53", 0, "Security Groups act as stateful virtual firewalls controlling traffic to EC2 instances.")
                ));

        // Course 20: Networking Basics
        Course c20 = saveCourse("networking-basics", "Networking Basics",
                "Learn the OSI model, TCP/IP, IPv4/IPv6, subnets, CIDR notation, DNS, HTTP/HTTPS, and routing fundamentals for cloud engineers.",
                "cloud-engineer", "Cloud Engineer", "Networking", QuestionDifficulty.BEGINNER, "3 hours", "bi-diagram-3", "warning");
        addLesson(c20, 1, "The OSI Model, TCP/IP, DNS & Subnetting", 25,
                "• Master the 7 layers of the OSI model\n• Understand TCP three-way handshake vs UDP\n• Calculate subnets using CIDR notation (e.g., 10.0.0.0/16)",
                "Computer networking connects computers, applications, and cloud environments together. A strong grasp of IP addressing, routing, and DNS is critical for cloud engineers.",
                "### OSI 7-Layer Model\n\n1. Physical -> 2. Data Link -> 3. Network (IP, Routers) -> 4. Transport (TCP, UDP) -> 5. Session -> 6. Presentation -> 7. Application (HTTP, DNS, SSH).\n\n### TCP vs UDP\n\n- **TCP**: Connection-oriented, guaranteed delivery, ordered packets (3-way handshake: SYN, SYN-ACK, ACK).\n- **UDP**: Connectionless, lightweight, no delivery guarantees (used in video streaming, VoIP, DNS queries).",
                "# Common Port Numbers Every Cloud Engineer Must Know\n# 22   : SSH (Secure Shell)\n# 53   : DNS (Domain Name System)\n# 80   : HTTP (Web traffic)\n# 443  : HTTPS (Encrypted web traffic)\n# 3306 : MySQL Database\n# 8080 : Spring Boot application default",
                "• CIDR `/24` gives 256 IP addresses (251 usable in AWS VPC because 5 are reserved).\n• DNS translates human-friendly domains (skillbridge.ai) to machine-readable IP addresses.",
                "TCP delivers reliable, ordered packets; IP addresses and subnets route traffic across global networks.",
                "1. What is the difference between TCP and UDP?\n2. How many usable host IP addresses are in a /24 subnet?",
                List.of(
                        new QuizQuestionCreateRequest(1, "Which transport protocol guarantees reliable, ordered packet delivery via a three-way handshake?", "UDP", "TCP", "ICMP", "ARP", 1, "TCP establishes a connection and guarantees delivery."),
                        new QuizQuestionCreateRequest(2, "Which default port is standard for encrypted web traffic using HTTPS?", "Port 22", "Port 80", "Port 443", "Port 8080", 2, "Port 443 is the standard port for HTTPS.")
                ));

        // Course 21: Linux Fundamentals
        Course c21 = saveCourse("linux-fundamentals", "Linux Fundamentals",
                "Master Linux command line, file system hierarchy, permissions (chmod/chown), processes, package managers, and shell scripting.",
                "cloud-engineer", "Cloud Engineer", "Linux", QuestionDifficulty.BEGINNER, "3 hours", "bi-terminal", "warning");
        addLesson(c21, 1, "Linux Shell Commands, Permissions & Process Management", 25,
                "• Navigate directory trees and manage files (ls, cd, cp, rm, grep, find)\n• Configure user permissions using chmod (rwx) and chown\n• Inspect and manage processes (ps, top, kill, systemctl)",
                "Linux powers over 90% of cloud servers, containers, and internet infrastructure. Fluency in the bash shell is a non-negotiable skill for cloud and DevOps engineers.",
                "### File Permissions Structure\n\nPermissions are divided into three tiers: **Owner (u)**, **Group (g)**, and **Others (o)**.\nEach tier has three permissions: **Read (4)**, **Write (2)**, and **Execute (1)**.\n\nExample: `chmod 755 app.sh` grants Owner `rwx` (7), Group `r-x` (5), and Others `r-x` (5).",
                "# Essential Linux Commands\npwd                  # Print current working directory\nls -la               # List all files with permissions and hidden items\nchmod 600 key.pem    # Read/write for owner only (required for SSH keys)\nps aux | grep java   # Find running Java processes\nsystemctl status app # Check systemd service status",
                "• Never run commands as `root` unless strictly required; use `sudo`.\n• Always verify file permissions on SSH private keys (`chmod 400` or `600`).",
                "Linux shell commands and numeric permissions form the backbone of cloud server administration.",
                "1. What do the numeric permissions 755 represent in Linux?\n2. What is the difference between ps and top?",
                List.of(
                        new QuizQuestionCreateRequest(1, "What permissions does 'chmod 755 script.sh' grant to the file owner?", "Read and Write only", "Read, Write, and Execute (rwx)", "Execute only", "Read only", 1, "7 in octal (4+2+1) grants read, write, and execute permissions."),
                        new QuizQuestionCreateRequest(2, "Which Linux command displays active processes and real-time CPU/memory consumption interactively?", "df", "top", "chmod", "curl", 1, "top (and htop) displays an interactive real-time process viewer.")
                ));

        // Course 22: Docker Fundamentals
        Course c22 = saveCourse("docker-fundamentals", "Docker Fundamentals",
                "Learn containerization, Dockerfiles, building images, running containers, volumes, networks, and Docker Compose for multi-container apps.",
                "cloud-engineer", "Cloud Engineer", "Docker", QuestionDifficulty.BEGINNER, "3.5 hours", "bi-box-seam", "warning");
        addLesson(c22, 1, "Containers, Dockerfiles, Volumes & Docker Compose", 30,
                "• Understand differences between Virtual Machines and Containers\n• Write clean multi-stage Dockerfiles\n• Manage container storage with persistent volumes\n• Orchestrate multi-tier applications with Docker Compose",
                "Docker packages applications and their complete runtime dependencies into lightweight, immutable containers, ensuring reproducible behavior across developer laptops and production servers.",
                "### Virtual Machines vs Containers\n\n- **Virtual Machines**: Emulate hardware, run a full guest operating system, and require substantial RAM and storage.\n- **Docker Containers**: Share the host OS kernel, isolate processes via Linux namespaces and cgroups, and start in seconds.\n\n### Persistent Data with Volumes\n\nContainers are ephemeral: when destroyed, internal state is lost. Docker Volumes map persistent storage from the host filesystem into container directories (e.g. `mysql_data:/var/lib/mysql`).",
                "# Sample Multi-Stage Dockerfile for Spring Boot\nFROM eclipse-temurin:17-jdk-jammy AS build\nWORKDIR /app\nCOPY . .\nRUN ./mvnw clean package -DskipTests\n\nFROM eclipse-temurin:17-jre-jammy\nWORKDIR /app\nCOPY --from=build /app/target/*.jar app.jar\nEXPOSE 8080\nENTRYPOINT [\"java\", \"-jar\", \"app.jar\"]",
                "• Use multi-stage Docker builds to keep production images tiny and secure (exclude compilers and source code).\n• Always persist database data using named Docker volumes.",
                "Containers provide lightweight isolation sharing the host kernel. Docker Compose orchestrates multi-service stacks seamlessly.",
                "1. Why are Docker containers faster to start than virtual machines?\n2. What happens to data stored inside a container without a mounted volume when the container is deleted?",
                List.of(
                        new QuizQuestionCreateRequest(1, "What is the primary architectural difference between Docker containers and Virtual Machines?", "Containers require their own complete guest OS kernel", "Containers share the host operating system kernel and isolate processes", "Containers can only run Python code", "Containers require hypervisor hardware emulation", 1, "Containers share the host OS kernel, making them lightweight and fast to start."),
                        new QuizQuestionCreateRequest(2, "How do you ensure MySQL database records survive container restarts and rebuilds in Docker Compose?", "By committing the container to a new image every hour", "By configuring a persistent named volume mapped to /var/lib/mysql", "By leaving the container running forever", "Databases in Docker automatically persist everywhere", 1, "Named volumes mount persistent host storage into container directories.")
                ));
    }

    // =========================================================================
    // 5. AI/ML ENGINEER COURSES (5 Courses)
    // =========================================================================
    private void seedAiMlEngineerCourses() {
        // Course 23: Python for AI
        Course c23 = saveCourse("python-for-ai", "Python for AI",
                "Master Python libraries and patterns specifically designed for Artificial Intelligence: vectorized tensors, Scikit-Learn, PyTorch basics, and GPU computing.",
                "aiml-engineer", "AI/ML Engineer", "Python for AI", QuestionDifficulty.BEGINNER, "3 hours", "bi-robot", "danger");
        addLesson(c23, 1, "Tensor Computation and Python AI Toolchains", 25,
                "• Understand multi-dimensional tensors vs matrices\n• Learn PyTorch tensor operations and autograd\n• Profile CPU vs GPU tensor execution with CUDA",
                "Artificial Intelligence relies on Python as its primary interface. Modern AI frameworks leverage multi-dimensional tensor representations and automated differentiation engines.",
                "### Tensors in AI\n\n- 0D: Scalar (e.g. single loss value `0.45`)\n- 1D: Vector (e.g. feature embedding `[0.2, 0.8, -0.4]`)\n- 2D: Matrix (e.g. batch of feature vectors)\n- 3D: Sequence / Image channel tensor (`[Channels, Height, Width]`)\n- 4D: Batches of images (`[BatchSize, Channels, Height, Width]`)",
                "# Tensor operations walkthrough\n# import torch\n# x = torch.tensor([1.0, 2.0, 3.0], requires_grad=True)\n# y = x * 2 + 1\n# y.sum().backward()\n# print(\"Gradients on x:\", x.grad)",
                "• `requires_grad=True` instructs autograd engines to track computation graphs for backpropagation.\n• Always move tensors to GPU devices (`cuda` or `mps`) during deep learning training.",
                "Tensors generalize arrays to N dimensions. Automatic differentiation calculates gradients essential for neural network training.",
                "1. What is the role of an autograd engine in AI frameworks?\n2. Why are GPUs preferred over CPUs for tensor computations?",
                List.of(
                        new QuizQuestionCreateRequest(1, "What does the PyTorch attribute 'requires_grad=True' tell the autograd engine to do?", "Compile the tensor into native C++ code", "Track operations on the tensor to compute analytical gradients during backpropagation", "Prevent modification of the tensor elements", "Move the tensor to GPU memory automatically", 1, "requires_grad=True builds a computation graph to calculate backward gradients."),
                        new QuizQuestionCreateRequest(2, "Why are graphics processing units (GPUs) significantly faster than CPUs for training neural networks?", "GPUs have higher clock speeds on individual cores", "GPUs possess thousands of parallel cores optimized for matrix multiplications", "GPUs have larger cache sizes", "GPUs bypass system memory completely", 1, "GPUs feature thousands of cores executing parallel matrix math.")
                ));

        // Course 24: Mathematics for Machine Learning
        Course c24 = saveCourse("mathematics-for-machine-learning", "Mathematics for Machine Learning",
                "Learn the essential mathematical foundations: Linear Algebra, Matrix Multiplication, Eigenvalues, Multivariate Calculus, Gradients, and Probability.",
                "aiml-engineer", "AI/ML Engineer", "Mathematics & Linear Algebra", QuestionDifficulty.INTERMEDIATE, "4 hours", "bi-calculator", "danger");
        addLesson(c24, 1, "Linear Algebra, Vectors, Matrices & Gradient Descent", 30,
                "• Master matrix multiplication dimensions and dot products\n• Understand partial derivatives and gradient vectors\n• Learn Gradient Descent optimization dynamics",
                "Machine learning algorithms are mathematical functions optimized over parameter spaces. Linear algebra represents the data, and calculus guides the optimization.",
                "### Key Mathematical Tools\n\n- **Dot Product**: $a \\cdot b = \\sum a_i b_i$. Measures similarity between two vectors.\n- **Gradient Vector ($\\nabla f$)**: Vector of partial derivatives pointing in the direction of steepest ascent.\n- **Gradient Descent**: Updates parameters $\\theta$ iteratively in the opposite direction of the gradient: $\\theta_{new} = \\theta_{old} - \\alpha \\nabla J(\\theta)$, where $\\alpha$ is the learning rate.",
                "# Gradient Descent Parameter Update Rule\n# theta = theta - (learning_rate * gradient)\n# If learning rate is too large: divergence and oscillations\n# If learning rate is too small: extremely slow convergence",
                "• In matrix multiplication $A \\times B$, columns of $A$ must equal rows of $B$.\n• The learning rate is the most critical hyperparameter in gradient descent.",
                "Linear algebra structures data inputs and model weights. Gradient descent minimizes loss by moving against the gradient direction.",
                "1. What happens if the learning rate in Gradient Descent is set too high?\n2. What does the dot product between two normalized vectors represent?",
                List.of(
                        new QuizQuestionCreateRequest(1, "In Gradient Descent, what happens when the learning rate hyperparameter is configured too high?", "Training converges instantaneously", "The algorithm may overshoot the minimum and diverge or oscillate uncontrollably", "The loss becomes exactly zero", "Gradients are calculated without partial derivatives", 1, "Excessive learning rates cause parameter divergence and instability."),
                        new QuizQuestionCreateRequest(2, "What dimensional condition must be satisfied to multiply matrix A (dimension m x k) by matrix B (dimension p x n)?", "m must equal n", "k must equal p (columns of A must equal rows of B)", "k must equal m", "m and n must be equal", 1, "Matrix multiplication requires the inner dimensions to match (k = p).")
                ));

        // Course 25: Machine Learning Fundamentals (AI Track)
        Course c25 = saveCourse("ml-algorithms-and-tuning", "Machine Learning Algorithms & Hyperparameter Tuning",
                "Master Random Forests, Gradient Boosted Trees (XGBoost/LightGBM), Support Vector Machines, cross-validation, and hyperparameter tuning.",
                "aiml-engineer", "AI/ML Engineer", "Model Evaluation", QuestionDifficulty.INTERMEDIATE, "3.5 hours", "bi-sliders2", "danger");
        addLesson(c25, 1, "Ensemble Methods, Random Forests & Gradient Boosting", 25,
                "• Understand Bagging vs Boosting concepts\n• Master Random Forests decision tree aggregation\n• Learn Gradient Boosting mechanics (XGBoost)",
                "Ensemble learning combines multiple base models to produce a single superior predictive model with lower variance and lower bias.",
                "### Bagging vs Boosting\n\n- **Bagging (Bootstrap Aggregating)**: Trains many independent trees in parallel on random data subsets and averages their votes (e.g. Random Forest). Reduces variance.\n- **Boosting**: Trains trees sequentially, where each new tree specifically focuses on correcting the residual errors of preceding trees (e.g. XGBoost, LightGBM). Reduces bias.",
                "# Ensemble comparison\n# Random Forest = Bagging (parallel trees, reduces variance)\n# Gradient Boosting = Boosting (sequential residual fitting, reduces bias)",
                "• Random Forests are resistant to overfitting; Gradient Boosting requires early stopping to prevent memorizing training noise.\n• Feature importance analysis helps understand which inputs drive predictions.",
                "Bagging trains trees in parallel to reduce variance; Boosting trains trees sequentially to reduce bias and minimize residuals.",
                "1. What is the key difference between Bagging and Boosting?\n2. Why are Random Forests less prone to overfitting than single decision trees?",
                List.of(
                        new QuizQuestionCreateRequest(1, "How does Gradient Boosting differ from Random Forest bagging?", "Boosting trains trees sequentially to correct prior errors, while Random Forest trains trees in parallel", "Random Forest only works on text data", "Boosting cannot be used for regression", "There is no difference", 0, "Boosting builds sequential trees targeting residual errors of previous iterations."),
                        new QuizQuestionCreateRequest(2, "What is the primary benefit of combining multiple decision trees in a Random Forest?", "It reduces model variance and prevents overfitting compared to a single deep tree", "It eliminates the need for test data", "It reduces computation time to zero", "It removes missing values automatically", 0, "Averaging predictions across decorrelated trees reduces prediction variance.")
                ));

        // Course 26: Neural Networks
        Course c26 = saveCourse("neural-networks", "Neural Networks",
                "Learn artificial neural network architecture, Perceptrons, activation functions (ReLU, Sigmoid, Softmax), forward propagation, and backpropagation.",
                "aiml-engineer", "AI/ML Engineer", "Deep Learning & Neural Networks", QuestionDifficulty.INTERMEDIATE, "4 hours", "bi-diagram-2", "danger");
        addLesson(c26, 1, "Perceptrons, Activation Functions & Backpropagation", 30,
                "• Master artificial neuron structure: inputs, weights, bias, and activations\n• Understand non-linearities: ReLU, Leaky ReLU, Sigmoid, Softmax\n• Learn the chain rule of calculus in backpropagation",
                "Neural networks are inspired by biological nervous systems, consisting of layers of interconnected artificial nodes capable of learning complex non-linear representations.",
                "### Anatomy of an Artificial Neuron\n\n$z = \\sum (w_i x_i) + b$, followed by activation $a = \\sigma(z)$.\nWithout non-linear activation functions, a multi-layer neural network collapses mathematically into a single linear regression regardless of depth!\n\n### Activation Functions\n\n- **ReLU (Rectified Linear Unit)**: $f(x) = \\max(0, x)$. Standard for hidden layers because it avoids vanishing gradients.\n- **Sigmoid**: Compresses output to $(0, 1)$ range; ideal for binary classification output.\n- **Softmax**: Converts a vector of logits into a probability distribution summing to 1.0.",
                "# PyTorch Neural Network Definition\n# import torch.nn as nn\n# class Classifier(nn.Module):\n#     def __init__(self):\n#         super().__init__()\n#         self.net = nn.Sequential(\n#             nn.Linear(20, 64),\n#             nn.ReLU(),\n#             nn.Linear(64, 2)\n#         )\n#     def forward(self, x):\n#         return self.net(x)",
                "• Vanishing gradients occur when deep networks use Sigmoid or Tanh activations in hidden layers.\n• ReLU gradients are 1 for positive inputs, avoiding gradient saturation.",
                "Non-linear activation functions enable neural networks to approximate arbitrary functions. Backpropagation applies the chain rule to update weights.",
                "1. Why are non-linear activation functions required in multi-layer neural networks?\n2. What problem does ReLU solve compared to Sigmoid in deep architectures?",
                List.of(
                        new QuizQuestionCreateRequest(1, "What would happen if a multi-layer deep neural network used only linear activation functions?", "It would become impossible to train", "The entire network would collapse into an equivalent single-layer linear model", "It would achieve 100% accuracy on every dataset", "It would run backwards", 1, "Compositions of linear functions remain linear, eliminating the ability to model complex curves."),
                        new QuizQuestionCreateRequest(2, "Which activation function is most commonly used in hidden layers of modern deep networks to combat vanishing gradients?", "Sigmoid", "ReLU (Rectified Linear Unit)", "Step Function", "Binary Threshold", 1, "ReLU maintains a constant gradient of 1 for positive values, mitigating vanishing gradients.")
                ));

        // Course 27: Deep Learning Fundamentals
        Course c27 = saveCourse("deep-learning-fundamentals", "Deep Learning Fundamentals",
                "Explore Convolutional Neural Networks (CNNs) for computer vision, Transformers and Attention mechanisms for NLP, and modern generative AI models.",
                "aiml-engineer", "AI/ML Engineer", "Deep Learning & Neural Networks", QuestionDifficulty.ADVANCED, "4 hours", "bi-lightbulb", "danger");
        addLesson(c27, 1, "CNNs, Attention Mechanisms & Transformer Architectures", 30,
                "• Master convolutional kernels, stride, and pooling for vision\n• Understand Self-Attention mechanisms: Query, Key, and Value\n• Learn the Transformer architecture behind modern LLMs",
                "Deep Learning powers the current AI revolution. From image recognition with CNNs to human-level text generation with Transformers, deep architectures model intricate multidimensional patterns.",
                "### Convolutional Neural Networks (CNNs)\n\nCNNs use sliding filters (kernels) to preserve spatial relationships in image data, extracting edges, textures, and hierarchical visual patterns.\n\n### Transformers & Self-Attention\n\nIntroduced in 'Attention Is All You Need' (2017), Transformers replaced recurrent networks by processing entire sequences in parallel:\n$$\\text{Attention}(Q, K, V) = \\text{softmax}\\left(\\frac{QK^T}{\\sqrt{d_k}}\\right)V$$\nThis self-attention mechanism enables models like Gemini and GPT to contextualize words across extensive document contexts.",
                "# Transformer Self-Attention Concept\n# Query (Q): What am I looking for?\n# Key (K): What information do I have?\n# Value (V): What is the actual content?\n# Dot product of Q and K yields attention weights indicating relevant context tokens",
                "• Transformers scale with parallel compute because they eliminate sequential RNN loops.\n• Pretraining on massive corpora followed by fine-tuning yields powerful foundation models.",
                "CNNs extract spatial features using convolution; Transformers model token interactions across global sequence contexts using self-attention.",
                "1. What is the role of the Self-Attention mechanism in Transformer architectures?\n2. Why did Transformers replace Recurrent Neural Networks (RNNs) for language modeling?",
                List.of(
                        new QuizQuestionCreateRequest(1, "In the Transformer Attention formula Attention(Q, K, V), what do Q, K, and V represent?", "Quantization, Kernel, Vector", "Query, Key, and Value", "Queue, Known, Variable", "Quality, Knowledge, Verification", 1, "Q, K, and V represent Query, Key, and Value matrices."),
                        new QuizQuestionCreateRequest(2, "What was the main reason Transformers superseded Recurrent Neural Networks (RNNs) for NLP tasks?", "Transformers use less disk space", "Transformers process all sequence tokens simultaneously in parallel, enabling massive GPU scaling without sequential bottlenecks", "Transformers only work on audio data", "RNNs were banned by open source licenses", 1, "Parallel token computation eliminated sequential bottlenecks, allowing training on massive internet-scale data.")
                ));
    }

    // =========================================================================
    // 6. CYBERSECURITY ANALYST COURSES (5 Courses)
    // =========================================================================
    private void seedCybersecurityCourses() {
        // Course 28: Cybersecurity Fundamentals
        Course c28 = saveCourse("cybersecurity-fundamentals", "Cybersecurity Fundamentals",
                "Learn the CIA Triad (Confidentiality, Integrity, Availability), threat actors, attack vectors, social engineering, and defense-in-depth.",
                "cybersecurity-analyst", "Cybersecurity Analyst", "Security fundamentals", QuestionDifficulty.BEGINNER, "2.5 hours", "bi-shield", "secondary");
        addLesson(c28, 1, "The CIA Triad, Threat Vectors & Defense in Depth", 25,
                "• Understand Confidentiality, Integrity, and Availability\n• Identify attack vectors: Phishing, Ransomware, Man-in-the-Middle\n• Implement layered defense-in-depth security strategies",
                "Cybersecurity is the practice of protecting systems, networks, and programs from digital attacks, unauthorized access, and malicious destruction.",
                "### The CIA Triad\n\n1. **Confidentiality**: Ensuring data is accessible only to authorized parties (e.g. encryption, access control).\n2. **Integrity**: Safeguarding the accuracy and completeness of data against unauthorized tampering (e.g. cryptographic hashes, checksums).\n3. **Availability**: Ensuring authorized users have reliable, timely access to information and systems (e.g. redundancy, DDoS mitigation).\n\n### Defense-in-Depth\n\nA layered defensive strategy deploying multiple security controls (firewalls, endpoint detection, authentication, encryption, physical security) so that failure of one layer does not compromise the enterprise.",
                "# Defense in Depth Layers:\n# Layer 1: Perimeter (Firewall, DDoS protection)\n# Layer 2: Network (VPC segmentation, IDS/IPS)\n# Layer 3: Host (OS hardening, Antivirus/EDR)\n# Layer 4: Application (Input validation, JWT authentication)\n# Layer 5: Data (AES-256 encryption at rest and in transit)",
                "• Security is a process, not a product: humans are frequently the weakest link via social engineering.\n• Assume breach: design systems so an intruder in one segment cannot pivot unimpeded.",
                "The CIA triad provides the framework for risk assessment. Layered defense-in-depth mitigates exposure at every system tier.",
                "1. What is the difference between data confidentiality and data integrity?\n2. What is the philosophy behind Defense-in-Depth?",
                List.of(
                        new QuizQuestionCreateRequest(1, "Which pillar of the CIA Triad is violated when an unauthorized attacker alters records in a student database?", "Confidentiality", "Integrity", "Availability", "Non-repudiation", 1, "Integrity ensures data is accurate, authentic, and protected against unauthorized modification."),
                        new QuizQuestionCreateRequest(2, "What is the primary principle of a Defense-in-Depth security architecture?", "Using a single impenetrable firewall", "Deploying multiple redundant, layered security controls across all system tiers", "Disconnecting all servers from the internet permanently", "Relying entirely on user passwords", 1, "Defense-in-depth ensures that if one security barrier fails, subsequent layers continue to protect assets.")
                ));

        // Course 29: Computer Networks (Security Track)
        Course c29 = saveCourse("computer-networks-security", "Computer Networks & Security",
                "Deep dive into network traffic inspection, firewalls, Intrusion Detection Systems (IDS/IPS), Wireshark packet analysis, and VPN protocols.",
                "cybersecurity-analyst", "Cybersecurity Analyst", "Network Security", QuestionDifficulty.INTERMEDIATE, "3.5 hours", "bi-ethernet", "secondary");
        addLesson(c29, 1, "Packet Analysis, Firewalls & Network Attack Vectors", 30,
                "• Inspect network traffic using packet capture concepts\n• Understand stateless vs stateful packet inspection firewalls\n• Analyze ARP poisoning, DNS spoofing, and SYN flood attacks",
                "Network security defends the transport infrastructure against interception, eavesdropping, and distributed denial of service.",
                "### Network Defense Technologies\n\n- **Firewalls**: Stateful firewalls track active connection states (e.g. allowing return traffic for established outbound requests automatically).\n- **IDS vs IPS**: Intrusion Detection Systems (IDS) detect and alert on anomalous patterns; Intrusion Prevention Systems (IPS) actively drop malicious packets inline in real time.\n- **SYN Flood Attack**: Exploits the TCP three-way handshake by sending thousands of SYN packets without completing the connection with ACK, exhausting server connection tables.",
                "# Wireshark packet capture filter examples:\n# tcp.port == 443    # Inspect encrypted HTTPS traffic\n# ip.addr == 192.168.1.1 # Filter by IP address\n# tcp.flags.syn == 1 and tcp.flags.ack == 0 # Detect SYN scans",
                "• Enforce network segmentation (VLANs/subnets) to isolate sensitive database tiers from public web endpoints.\n• Disable unused ports and protocols on network interfaces.",
                "Stateful firewalls and IPS systems inspect packet headers and payloads. Network segmentation limits breach blast radiuses.",
                "1. What is the difference between an IDS and an IPS?\n2. How does a TCP SYN flood attack cause denial of service?",
                List.of(
                        new QuizQuestionCreateRequest(1, "What is the fundamental difference between an Intrusion Detection System (IDS) and an Intrusion Prevention System (IPS)?", "IDS runs on Windows; IPS runs on Linux", "An IDS only detects and alerts on attacks, whereas an IPS actively blocks/drops malicious packets in real time", "An IDS encrypts files; an IPS decrypts them", "There is no functional difference", 1, "An IDS passively alerts on suspicious traffic; an IPS actively intervenes inline to drop malicious traffic."),
                        new QuizQuestionCreateRequest(2, "How does a TCP SYN flood attack exhaust target server resources?", "By sending corrupt DNS queries", "By flooding the target with SYN packets and never sending the final ACK, filling the connection backlog table", "By physically disconnecting network cables", "By changing server passwords", 1, "Unacknowledged half-open TCP connections fill the operating system backlog table.")
                ));

        // Course 30: Linux Security
        Course c30 = saveCourse("linux-security", "Linux Security & Hardening",
                "Master Linux server hardening, SSH security, sudoers configuration, iptables/UFW firewalls, SELinux/AppArmor, and security auditing.",
                "cybersecurity-analyst", "Cybersecurity Analyst", "Linux Security", QuestionDifficulty.INTERMEDIATE, "3 hours", "bi-shield-shaded", "secondary");
        addLesson(c30, 1, "Server Hardening, SSH Security & Access Controls", 25,
                "• Harden SSH configurations (disable root login, enforce key authentication)\n• Configure host firewalls with UFW and iptables\n• Audit logs in /var/log/auth.log and journalctl",
                "Securing production Linux hosts requires systematically eliminating default attack vectors, enforcing strict least-privilege permissions, and continuous log auditing.",
                "### Core Hardening Checklist\n\n1. **SSH Hardening** in `/etc/ssh/sshd_config`:\n   - `PermitRootLogin no`\n   - `PasswordAuthentication no` (require SSH keys)\n   - Change default port 22 if appropriate.\n2. **Firewall (UFW)**: Default deny all incoming traffic; permit only required ports (`80`, `443`, custom SSH).\n3. **Fail2ban**: Automatically ban IP addresses that exhibit repeated authentication failures.",
                "# Recommended /etc/ssh/sshd_config Hardening\n# PermitRootLogin no\n# PasswordAuthentication no\n# PubkeyAuthentication yes\n# MaxAuthTries 3\n\n# UFW Firewall Setup Commands\n# sudo ufw default deny incoming\n# sudo ufw default allow outgoing\n# sudo ufw allow 22/tcp\n# sudo ufw allow 443/tcp\n# sudo ufw enable",
                "• Regularly update system packages using security patches (`apt update && apt upgrade -s`).\n• Audit user accounts with `sudo` privileges in `/etc/sudoers`.",
                "Hardening Linux involves disabling root logins, enforcing SSH keys, configuring host firewalls, and continuous log surveillance.",
                "1. Why should direct root SSH login be disabled in production?\n2. What does Fail2ban do on a Linux server?",
                List.of(
                        new QuizQuestionCreateRequest(1, "Which setting in /etc/ssh/sshd_config prevents direct root logins over SSH?", "DisableRootLogin yes", "PermitRootLogin no", "AllowUsers non-root", "DenyRoot access", 1, "PermitRootLogin no ensures administrators must log in as individual users first."),
                        new QuizQuestionCreateRequest(2, "What is the primary function of Fail2ban on a Linux server?", "Compiling kernels", "Monitoring logs and dynamically banning IP addresses that show signs of automated brute-force attacks", "Formatting hard drives", "Compressing database backups", 1, "Fail2ban monitors logs for repeated failed logins and creates temporary firewall drop rules.")
                ));

        // Course 31: Cryptography Fundamentals
        Course c31 = saveCourse("cryptography-fundamentals", "Cryptography Fundamentals",
                "Understand symmetric encryption (AES), asymmetric encryption (RSA, ECC), hashing functions (SHA-256), digital signatures, and PKI/TLS certificates.",
                "cybersecurity-analyst", "Cybersecurity Analyst", "Cryptography Basics", QuestionDifficulty.BEGINNER, "3.5 hours", "bi-key", "secondary");
        addLesson(c31, 1, "Symmetric vs Asymmetric Ciphers, Hashes & Digital Signatures", 30,
                "• Differentiate between symmetric (AES) and asymmetric (RSA/ECC) encryption\n• Master cryptographic hash functions: SHA-256 and salt\n• Understand Public Key Infrastructure (PKI) and digital certificates",
                "Cryptography is the mathematical science of securing communication against adversaries. It guarantees confidentiality, data integrity, and non-repudiation.",
                "### Symmetric vs Asymmetric Encryption\n\n- **Symmetric Encryption (e.g. AES-256)**: The same secret key is used for both encryption and decryption. Fast and ideal for bulk data.\n- **Asymmetric Encryption (e.g. RSA, ECC)**: Uses a key pair: a Public Key (distributed openly to encrypt) and a Private Key (kept secret to decrypt).\n\n### Cryptographic Hashing\n\nOne-way mathematical functions converting arbitrary input into fixed-length digests (e.g. SHA-256). Ideal for integrity verification and password storage (when salted with bcrypt).",
                "// Cryptographic Concept Walkthrough\n// 1. Password Storage: Never store plain text or simple MD5!\n// Always use slow, salted cryptographic hashes like BCrypt or Argon2:\n// hash = bcrypt.hash(password, salt);\n// 2. HTTPS/TLS Handshake: Uses asymmetric encryption to negotiate an ephemeral AES symmetric key",
                "• Encryption is two-way (can be decrypted with key); hashing is strictly one-way.\n• Always salt passwords before hashing to protect against precomputed rainbow table attacks.",
                "Symmetric ciphers encrypt bulk data rapidly; asymmetric ciphers securely exchange keys; hashes ensure data integrity.",
                "1. What is the fundamental difference between encryption and hashing?\n2. Why must passwords always be salted before being hashed?",
                List.of(
                        new QuizQuestionCreateRequest(1, "What is the key difference between symmetric and asymmetric encryption?", "Symmetric is slower than asymmetric", "Symmetric uses one shared key for encryption and decryption; asymmetric uses a public/private key pair", "Asymmetric encryption cannot be used on computers", "Symmetric encryption has no keys", 1, "Symmetric uses one shared key; asymmetric uses a public/private key pair."),
                        new QuizQuestionCreateRequest(2, "Why are random 'salts' added to user passwords before cryptographic hashing?", "To make the password easier to remember", "To ensure identical passwords produce unique hash digests and thwart rainbow table attacks", "To compress the password length", "To allow passwords to be recovered in plain text", 1, "Salts ensure identical passwords yield different hashes, neutralizing precomputed rainbow tables.")
                ));

        // Course 32: Security Monitoring
        Course c32 = saveCourse("security-monitoring", "Security Monitoring & Incident Response",
                "Learn Security Information and Event Management (SIEM), log aggregation, threat hunting, incident response lifecycles, and vulnerability management.",
                "cybersecurity-analyst", "Cybersecurity Analyst", "Security Operations", QuestionDifficulty.INTERMEDIATE, "3 hours", "bi-activity", "secondary");
        addLesson(c32, 1, "SIEM Systems, Threat Hunting & Incident Response Lifecycles", 25,
                "• Understand SIEM log aggregation and correlation rules\n• Master the NIST Incident Response lifecycle: Preparation, Detection, Containment, Eradication, Recovery, Lessons Learned\n• Identify Indicators of Compromise (IoCs)",
                "Security Operations Centers (SOC) monitor systems 24/7 to detect security events, contain active intrusions, and restore business operations.",
                "### The NIST Incident Response Lifecycle\n\n1. **Preparation**: Hardening systems, defining playbooks, training incident response teams.\n2. **Detection & Analysis**: Identifying potential security alerts and verifying true vs false positives.\n3. **Containment**: Isolating affected systems to prevent lateral movement.\n4. **Eradication**: Removing malware, backdoors, and compromised credentials.\n5. **Recovery**: Restoring clean systems to production and testing operations.\n6. **Lessons Learned**: Conducting post-incident reviews to strengthen defenses.",
                "# Common Indicators of Compromise (IoCs) to Monitor in SIEM:\n# 1. Unusual outbound network connections to unknown foreign IPs\n# 2. Repeated after-hours privilege escalation attempts\n# 3. Sudden spikes in database record reads or data transfer volume\n# 4. Unexpected modifications to system startup binaries and services",
                "• When an intrusion is detected, contain the system immediately (e.g. isolate from network) before beginning eradication.\n• Maintain detailed chain-of-custody documentation for forensic evidence.",
                "SIEM platforms centralize and correlate enterprise event logs. The NIST incident response framework structures containment and recovery.",
                "1. What are the six phases of the NIST Incident Response lifecycle?\n2. What is an Indicator of Compromise (IoC)?",
                List.of(
                        new QuizQuestionCreateRequest(1, "In the NIST Incident Response framework, which phase immediately follows Detection & Analysis when a breach is confirmed?", "Lessons Learned", "Containment", "Eradication", "Preparation", 1, "Containment immediately follows detection to prevent attackers from moving laterally."),
                        new QuizQuestionCreateRequest(2, "What is the primary role of a SIEM (Security Information and Event Management) system in a Security Operations Center?", "Writing software applications", "Aggregating and correlating security event logs across an enterprise in real time to detect threats", "Backing up employee emails", "Managing employee payroll", 1, "SIEM platforms centralize logs and apply automated correlation rules to flag threats.")
                ));
    }

    // =========================================================================
    // HELPER CREATION METHODS
    // =========================================================================

    private Course saveCourse(String slug, String title, String description, String careerId,
                              String careerTitle, String skillCategory, QuestionDifficulty difficulty,
                              String duration, String icon, String badgeColor) {
        return courseRepository.findBySlugIgnoreCase(slug).orElseGet(() ->
                courseRepository.save(new Course(slug, title, description, careerId, careerTitle,
                        skillCategory, difficulty, duration, icon, badgeColor)));
    }

    private void addLesson(Course course, int order, String title, int minutes,
                           String objectives, String intro, String content,
                           String code, String concepts, String summary,
                           String practice, List<QuizQuestionCreateRequest> quiz) {
        Lesson lesson = lessonRepository.findByCourseIdAndLessonOrder(course.getId(), order).orElseGet(() ->
                lessonRepository.save(new Lesson(course, title, order, minutes, objectives, intro,
                        content, code, concepts, summary, practice)));

        // Add quiz questions if not present
        if (quizQuestionRepository.countByLessonId(lesson.getId()) == 0 && quiz != null && !quiz.isEmpty()) {
            List<LessonQuizQuestion> list = new ArrayList<>();
            for (QuizQuestionCreateRequest q : quiz) {
                list.add(new LessonQuizQuestion(
                        lesson,
                        q.questionOrder(),
                        q.questionText(),
                        q.optionA(),
                        q.optionB(),
                        q.optionC(),
                        q.optionD(),
                        q.correctOption(),
                        q.explanation()
                ));
            }
            quizQuestionRepository.saveAll(list);
        }
    }
}
