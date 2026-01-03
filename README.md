<h1>Leave Management System (Spring Boot)</h1>

<p>
A backend REST API for managing employee leave requests with 
<strong>role-based access control</strong>, 
<strong>supervisor hierarchies</strong>, and 
<strong>approval workflows</strong>.
</p>

<hr/>

<h2>Tech Stack</h2>
<ul>
  <li><strong>Java 17+</strong></li>
  <li><strong>Spring Boot</strong></li>
  <li>Spring Web (REST APIs)</li>
  <li>Spring Data JPA (Hibernate)</li>
  <li>Spring Security + JWT Authentication</li>
  <li>MySQL</li>
  <li>Flyway (Database migrations)</li>
  <li>MapStruct (DTO mapping)</li>
  <li>Lombok</li>
</ul>

<hr/>

<h2>Core Features</h2>

<h3>Authentication & Authorization</h3>
<ul>
  <li>JWT-based authentication</li>
  <li>Role-based access control (ADMIN / USER)</li>
  <li>Secure endpoints using Spring Security</li>
</ul>

<h3>Leave Management</h3>
<ul>
  <li>Create leave requests</li>
  <li>Update leave details (PATCH support)</li>
  <li>Cancel leave requests with validation rules</li>
  <li>Approve / Reject leave requests</li>
  <li>Half-day and full-day leave handling</li>
  <li>Leave date validation (no past dates, valid ranges)</li>
</ul>

<h3>Supervisor Hierarchy</h3>
<ul>
  <li>Primary supervisor (only one per user)</li>
  <li>Multiple secondary supervisors</li>
  <li>Recursive supervisor → subordinate relationships</li>
  <li>Fetch subordinate and nested-subordinate leaves</li>
</ul>

<h3>Admin Capabilities</h3>
<ul>
  <li>View all leave requests</li>
  <li>Approve / reject any leave</li>
  <li>Paginated & sorted leave listings</li>
</ul>

<hr/>

<h2>Architecture Overview</h2>

<ul>
  <li><strong>Controller Layer</strong> – Handles HTTP requests</li>
  <li><strong>Service Layer</strong> – Business logic & validation</li>
  <li><strong>Repository Layer</strong> – Database access via JPA</li>
  <li><strong>DTO + Mapper Layer</strong> – Clean API responses using MapStruct</li>
  <li><strong>Global Exception Handling</strong> – Centralized error responses</li>
</ul>

<hr/>

<h2>Database</h2>

<ul>
  <li>MySQL as primary database</li>
  <li>Flyway for version-controlled schema migrations</li>
  <li>Normalized tables for users, leaves, leave types, supervisors</li>
</ul>

<hr/>

<h2>API Capabilities</h2>

<ul>
  <li>Pagination for large datasets</li>
  <li>Filtering by status and date ranges</li>
  <li>Role-based endpoint restrictions</li>
  <li>Consistent error responses via global exception handler</li>
</ul>

<hr/>

<h2>Security</h2>

<ul>
  <li>JWT token-based authentication</li>
  <li>Stateless API design</li>
  <li>Protected endpoints based on roles</li>
</ul>

<hr/>

<h2>Why This Project?</h2>

<p>
This project demonstrates real-world backend engineering concepts:
</p>

<ul>
  <li>Clean layered architecture</li>
  <li>Proper exception handling</li>
  <li>Relationship modeling (one-to-many, many-to-one)</li>
  <li>Recursive data handling</li>
  <li>Enterprise-style validation rules</li>
</ul>

<hr/>

<h2>Future Improvements</h2>

<ul>
  <li>Email notifications for approvals</li>
  <li>Audit logging</li>
  <li>Advanced reporting</li>
  <li>Caching frequently accessed data</li>
</ul>

<hr/>

<p>
<strong>Author:</strong> Chathuka Kavinda  
<br/>
<strong>Role:</strong> Junior Backend Developer
</p>
