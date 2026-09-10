package com.oibsip.quiz.data;

import com.oibsip.quiz.model.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Question Bank containing curated questions for the Quiz Application.
 * Topics: Computer Science, Programming, and Technology Fundamentals.
 */
public class QuestionBank {

    private static final List<Question> ALL_QUESTIONS = new ArrayList<>();

    static {
        // Question 1
        ALL_QUESTIONS.add(new Question(
                1,
                "What does CPU stand for in computer systems?",
                Arrays.asList(
                        "Central Processing Unit",
                        "Central Process Utility",
                        "Computer Primary Unit",
                        "Core Program Unit"
                ),
                0,
                "CPU stands for Central Processing Unit, the primary hardware component that executes computer program instructions.",
                "Computer Hardware",
                "Easy"
        ));

        // Question 2
        ALL_QUESTIONS.add(new Question(
                2,
                "Which data structure operates on a Last-In, First-Out (LIFO) principle?",
                Arrays.asList(
                        "Queue",
                        "Stack",
                        "Array",
                        "Binary Search Tree"
                ),
                1,
                "A Stack follows the LIFO (Last-In, First-Out) order, where the most recently added item is the first one removed.",
                "Data Structures",
                "Easy"
        ));

        // Question 3
        ALL_QUESTIONS.add(new Question(
                3,
                "Who is widely recognized as the father of modern Computer Science?",
                Arrays.asList(
                        "Charles Babbage",
                        "Alan Turing",
                        "John von Neumann",
                        "Ada Lovelace"
                ),
                1,
                "Alan Turing formalized the concepts of algorithm and computation with the Turing Machine and played a pivotal role in cracking the Enigma code.",
                "History of Computing",
                "Medium"
        ));

        // Question 4
        ALL_QUESTIONS.add(new Question(
                4,
                "Which programming language was developed by James Gosling at Sun Microsystems in 1995?",
                Arrays.asList(
                        "Python",
                        "C++",
                        "Java",
                        "C#"
                ),
                2,
                "Java was originally developed by James Gosling at Sun Microsystems and released in 1995 under the slogan 'Write Once, Run Anywhere'.",
                "Programming Languages",
                "Easy"
        ));

        // Question 5
        ALL_QUESTIONS.add(new Question(
                5,
                "What is the time complexity of searching for an element in a balanced Binary Search Tree (BST)?",
                Arrays.asList(
                        "O(1)",
                        "O(n)",
                        "O(log n)",
                        "O(n log n)"
                ),
                2,
                "Because a balanced BST halves the search space at each level, searching takes logarithmic time: O(log n).",
                "Algorithms",
                "Medium"
        ));

        // Question 6
        ALL_QUESTIONS.add(new Question(
                6,
                "What protocol is used to securely encrypt web traffic between browser and server?",
                Arrays.asList(
                        "FTP",
                        "HTTPS (SSL/TLS)",
                        "SMTP",
                        "SNMP"
                ),
                1,
                "HTTPS uses TLS/SSL (Transport Layer Security) encryption to protect HTTP communications against eavesdropping and tampering.",
                "Networking",
                "Easy"
        ));

        // Question 7
        ALL_QUESTIONS.add(new Question(
                7,
                "In Object-Oriented Programming, what concept allows a subclass to provide a specific implementation of a method defined in its parent class?",
                Arrays.asList(
                        "Method Overriding",
                        "Method Overloading",
                        "Encapsulation",
                        "Data Abstraction"
                ),
                0,
                "Method Overriding allows a child class to rewrite the implementation of an inherited parent method at runtime (dynamic polymorphism).",
                "OOP Concepts",
                "Medium"
        ));

        // Question 8
        ALL_QUESTIONS.add(new Question(
                8,
                "What does SQL stand for in database management?",
                Arrays.asList(
                        "Simple Query Language",
                        "Structured Query Language",
                        "Standard Question Lexicon",
                        "Sequential Query Logic"
                ),
                1,
                "SQL stands for Structured Query Language, the ANSI/ISO standard language used to query and manage relational databases.",
                "Databases",
                "Easy"
        ));

        // Question 9
        ALL_QUESTIONS.add(new Question(
                9,
                "Which type of primary computer memory is volatile and loses its data when powered off?",
                Arrays.asList(
                        "ROM",
                        "SSD",
                        "RAM",
                        "Optical Disk"
                ),
                2,
                "RAM (Random Access Memory) is volatile memory that requires continuous electrical power to maintain its stored data.",
                "Computer Hardware",
                "Easy"
        ));

        // Question 10
        ALL_QUESTIONS.add(new Question(
                10,
                "What does an IP address stand for in computer networking?",
                Arrays.asList(
                        "Internal Provider",
                        "Internet Protocol",
                        "Interconnect Pathway",
                        "Interface Point"
                ),
                1,
                "An IP address (Internet Protocol address) is a unique numerical label assigned to each device connected to a computer network.",
                "Networking",
                "Easy"
        ));

        // Question 11
        ALL_QUESTIONS.add(new Question(
                11,
                "In Git version control, which command is used to record local changes to the repository with a message?",
                Arrays.asList(
                        "git pull",
                        "git commit",
                        "git checkout",
                        "git branch"
                ),
                1,
                "'git commit' creates a snapshot of staged changes along with a descriptive commit message in the Git history.",
                "Developer Tools",
                "Easy"
        ));

        // Question 12
        ALL_QUESTIONS.add(new Question(
                12,
                "Which of the following is NOT an Operating System?",
                Arrays.asList(
                        "Linux",
                        "macOS",
                        "Windows",
                        "Apache"
                ),
                3,
                "Apache is an open-source HTTP web server software application, not an operating system.",
                "System Software",
                "Easy"
        ));

        // Question 13
        ALL_QUESTIONS.add(new Question(
                13,
                "What is the primary purpose of the Android Manifest (AndroidManifest.xml) file?",
                Arrays.asList(
                        "To style UI elements and themes",
                        "To declare app components, permissions, and metadata to the OS",
                        "To compile Java source code into bytecode",
                        "To manage Gradle third-party dependencies"
                ),
                1,
                "The AndroidManifest.xml declares essential information about the application to the Android system, including activities, services, and required permissions.",
                "Android Development",
                "Medium"
        ));

        // Question 14
        ALL_QUESTIONS.add(new Question(
                14,
                "In binary representation, what decimal number does the 8-bit byte 00001010 represent?",
                Arrays.asList(
                        "8",
                        "10",
                        "12",
                        "16"
                ),
                1,
                "In binary: (0 * 128) + (0 * 64) + (0 * 32) + (0 * 16) + (1 * 8) + (0 * 4) + (1 * 2) + (0 * 1) = 8 + 2 = 10.",
                "Digital Logic",
                "Medium"
        ));

        // Question 15
        ALL_QUESTIONS.add(new Question(
                15,
                "Which sorting algorithm has an average and worst-case time complexity of O(n log n)?",
                Arrays.asList(
                        "Bubble Sort",
                        "Insertion Sort",
                        "Merge Sort",
                        "Selection Sort"
                ),
                2,
                "Merge Sort utilizes a divide-and-conquer strategy that consistently achieves O(n log n) time complexity in worst, average, and best cases.",
                "Algorithms",
                "Medium"
        ));
    }

    /**
     * Returns an unmodifiable list of all registered questions in original order.
     */
    public static List<Question> getAllQuestions() {
        return Collections.unmodifiableList(ALL_QUESTIONS);
    }

    /**
     * Total number of questions available in the bank.
     */
    public static int getBankSize() {
        return ALL_QUESTIONS.size();
    }

    /**
     * Returns a randomized subset of questions with shuffled options.
     *
     * @param count Number of questions to retrieve (capped at total available)
     * @param shuffleOptions Whether to randomize the 4 options inside each question
     * @return List of prepared Question objects
     */
    public static List<Question> getShuffledQuestions(int count, boolean shuffleOptions) {
        List<Question> pool = new ArrayList<>(ALL_QUESTIONS);
        Collections.shuffle(pool);

        int sampleSize = Math.min(count, pool.size());
        List<Question> selected = pool.subList(0, sampleSize);

        if (!shuffleOptions) {
            return new ArrayList<>(selected);
        }

        List<Question> result = new ArrayList<>(sampleSize);
        for (Question q : selected) {
            result.add(q.createShuffledCopy());
        }
        return result;
    }
}
