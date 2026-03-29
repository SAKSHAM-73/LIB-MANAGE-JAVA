# Library Management System (LMS)
### Java 21 + JDBC + MySQL 8.0 (Docker) | KIIT CSE — 23053073


---

## FILE STRUCTURE

```
LMS/
├── sql/
│   └── schema.sql                  ← Run once to create DB + seed data
├── lib/
│   └── mysql-connector-j-*.jar     ← Place JDBC driver here (see Step 2)
├── src/
│   └── lms/
│       ├── Main.java               ← Entry point
│       ├── model/
│       │   ├── Book.java
│       │   ├── User.java           ← Role enum: ADMIN / USER
│       │   └── BorrowRecord.java
│       ├── exceptions/
│       │   ├── LMSException.java            ← Base exception
│       │   ├── BookNotFoundException.java
│       │   ├── BookUnavailableException.java
│       │   ├── UserNotFoundException.java
│       │   ├── InsufficientBalanceException.java
│       │   ├── InvalidCredentialsException.java
│       │   ├── DuplicateEntryException.java
│       │   └── UnauthorizedAccessException.java
│       ├── hash/
│       │   ├── BookHashTable.java   ← Custom hash table (linear probing)
│       │   └── PasswordUtil.java    ← SHA-256 hashing
│       ├── dao/
│       │   ├── DBConnection.java    ← MySQL JDBC singleton
│       │   ├── BookDAO.java
│       │   ├── UserDAO.java
│       │   ├── BorrowDAO.java
│       │   └── LibraryService.java  ← Business logic
│       └── ui/
│           └── ConsoleUI.java       ← Terminal menus (Guest/Admin/User)
└── README.md
```

---

## STEP-BY-STEP: HOW TO RUN

### Prerequisites 
- Java 21 via SDKMAN: `java -version` → should show GraalVM 21
- Docker running with `mysql-lab` container
- WSL2 Ubuntu terminal open

---

### STEP 1 — Start your MySQL Docker container

Open your Ubuntu terminal and run:

```bash
docker start mysql-lab
```

Verify it's running:
```bash
docker ps
# You should see mysql-lab listed and port 3306->3306
```

---

### STEP 2 — Download the MySQL JDBC Driver

In your Ubuntu terminal:

```bash
cd ~/study/          # or wherever you put the project
mkdir -p LMS/lib
cd LMS/lib

# Download MySQL Connector/J
wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar
```

Verify the file is there:
```bash
ls lib/
# mysql-connector-j-8.3.0.jar
```

---

### STEP 3 — Create the Database Schema

```bash
# Run the schema SQL inside your mysql-lab container
docker exec -i mysql-lab mysql -uroot -proot < sql/schema.sql
```

Verify tables were created:
```bash
docker exec -it mysql-lab mysql -uroot -proot -e "USE lms_db; SHOW TABLES;"
```

Expected output:
```
+------------------+
| Tables_in_lms_db |
+------------------+
| lms_books        |
| lms_borrows      |
| lms_users        |
+------------------+
```

---

### STEP 4 — Compile the Project

Navigate to the project root (where `src/` and `lib/` are):

```bash
cd ~/study/LMS

# Compile all Java files
javac -cp "lib/mysql-connector-j-8.3.0.jar" \
      -d out \
      $(find src -name "*.java")
```

You should see no errors. An `out/` folder will be created with compiled `.class` files.

---

### STEP 5 — Run the Application

```bash
java -cp "out:lib/mysql-connector-j-8.3.0.jar" lms.Main
```

You'll see the LMS banner and the Guest Menu appear.

**Default Admin Login:**
```
Email    : admin@lms.com
Password : admin
```

---

### STEP 6 — (Optional) Run from IntelliJ IDEA

Since you have IntelliJ connected to WSL:

1. Open terminal in Ubuntu → `cd ~/study/LMS` → `idea .`
2. In IntelliJ → **File → Project Structure → Libraries**
3. Click `+` → **Java** → select `lib/mysql-connector-j-8.3.0.jar`
4. Mark `src/` as **Sources Root** (right-click → Mark Directory As → Sources Root)
5. Set SDK to your WSL Java: `\\wsl$\Ubuntu\home\zen\.sdkman\candidates\java\current`
6. Run `Main.java` with the green ▶ button

---

### Quick daily startup (one command)

Add this alias to your `~/.bashrc`:

```bash
alias lms='docker start mysql-lab && cd ~/study/LMS && java -cp "out:lib/mysql-connector-j-8.3.0.jar" lms.Main'
```

Then just type `lms` in any terminal.

---

## PUTTING THE PROJECT ON GITHUB

### PART A — One-Time GitHub Setup (if not done)

**1. Create a GitHub account** at https://github.com (if you don't have one)

**2. Configure Git in WSL:**
```bash
git config --global user.name  "Your Name"
git config --global user.email "your@email.com"
```

**3. Generate SSH key for passwordless push:**
```bash
ssh-keygen -t ed25519 -C "your@email.com"
# Press Enter for all prompts (default location, no passphrase)

# Copy your public key
cat ~/.ssh/id_ed25519.pub
```

**4. Add SSH key to GitHub:**
- Go to https://github.com/settings/keys
- Click **New SSH key**
- Paste the output from the `cat` command above
- Click **Add SSH key**

**5. Test the connection:**
```bash
ssh -T git@github.com
# Should say: Hi <username>! You've successfully authenticated...
```

---

### PART B — Create the GitHub Repository

**1. Go to https://github.com/new**

Fill in:
- Repository name: `library-management-system`
- Description: `Java LMS with JDBC (MySQL), custom exceptions, and hashing — KIIT SCE`
- Visibility: **Public** (for portfolio) or Private
- ❌ Do NOT initialize with README (you already have one)
- Click **Create repository**

---

### PART C — Push Code from WSL

In your Ubuntu terminal:

```bash
cd ~/study/LMS

# Initialize git repo
git init

# Create .gitignore FIRST (very important — never commit compiled files or credentials)
cat > .gitignore << 'EOF'
# Compiled output
out/
*.class

# JDBC driver (too large, users download their own)
lib/

# IDE files
.idea/
*.iml
.vscode/

# OS files
.DS_Store
Thumbs.db
EOF

# Stage all files
git add .

# First commit
git commit -m "Initial commit: LMS with Java JDBC MySQL, custom exceptions, hashing"

# Connect to your GitHub repo (replace YOUR_USERNAME)
git remote add origin git@github.com:YOUR_USERNAME/library-management-system.git

# Push to GitHub
git branch -M main
git push -u origin main
```

Open your browser → go to your GitHub repo — the code will be there!

---

### PART D — Ongoing Git Workflow

After every coding session:

```bash
# Check what changed
git status

# Stage your changes
git add .

# Commit with a meaningful message
git commit -m "Add: borrow history display feature"

# Push to GitHub
git push
```

---

### PART E — Good Commit Message Format

For your portfolio and interviews:

```
Add: <new feature>          → git commit -m "Add: user balance top-up feature"
Fix: <bug description>      → git commit -m "Fix: fine calculation for same-day return"
Refactor: <what changed>    → git commit -m "Refactor: extract BookHashTable to separate class"
Docs: <what updated>        → git commit -m "Docs: update README with run instructions"
```

---

### PART F — Recommended GitHub README badges (copy-paste)

Add to the top of your README.md for a professional look:

```markdown
![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)
![Docker](https://img.shields.io/badge/Docker-mysql--lab-2496ED?logo=docker)
![JDBC](https://img.shields.io/badge/JDBC-MySQL%20Connector-green)
![License](https://img.shields.io/badge/license-MIT-brightgreen)
```

---

## WHAT CHANGED FROM ORACLE → MYSQL

| Aspect | Before (Oracle 21c) | After (MySQL Docker) |
|---|---|---|
| JDBC URL | `jdbc:oracle:thin:@localhost:1521:XE` | `jdbc:mysql://127.0.0.1:3306/lms_db` |
| Driver class | `oracle.jdbc.driver.OracleDriver` | `com.mysql.cj.jdbc.Driver` |
| Driver JAR | `ojdbc11.jar` | `mysql-connector-j-8.3.0.jar` |
| Auto-increment | `GENERATED ALWAYS AS IDENTITY` | `AUTO_INCREMENT` |
| ENUM type | `CHECK (role IN ('ADMIN','USER'))` | `ENUM('ADMIN','USER')` |
| Date default | `DEFAULT SYSDATE` | `DEFAULT CURRENT_TIMESTAMP` |
| Schema init | SQL*Plus | `docker exec mysql-lab mysql ...` |

All Java business logic, hashing, exceptions, and UI are **unchanged**.

---

## AGILE METHODOLOGY (Interview Answer)

**"I used Agile with 4 sprints of roughly 1 week each:"**

- **Sprint 1:** DB schema + model classes (Book, User, BorrowRecord)
- **Sprint 2:** DAO layer (BookDAO, UserDAO, BorrowDAO) + JDBC connection
- **Sprint 3:** Business logic (LibraryService) + all custom exceptions + hashing
- **Sprint 4:** UI layer (ConsoleUI) + end-to-end testing + README

Each sprint produced working, testable code. I adapted mid-project when I switched from Oracle to MySQL (because Oracle wasn't in my current dev setup), which is exactly how Agile handles change — respond to environment rather than following a fixed plan.
