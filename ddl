CREATE TABLE psfs.file_upload (
    id INT PRIMARY KEY AUTO_INCREMENT,
    upload_by VARCHAR(100),
    upload_month VARCHAR(50),
    file_name VARCHAR(500),
    file_content BLOB
);


 CREATE TABLE psfs.employee_salary (
    serial_no              VARCHAR(500),
    id_no                  VARCHAR(500),
    uan_no                 VARCHAR(500),
    esi_no                 VARCHAR(500),
    aadhar_card_no         VARCHAR(500),
    bank_ac_no             VARCHAR(500),
    gender                 VARCHAR(500),
    name_of_the_employee   VARCHAR(500),
    desingnation           VARCHAR(500),
    site_name              VARCHAR(500),
    total_days             VARCHAR(500),
    basic                  VARCHAR(500),
    d_a                    VARCHAR(500),
    hra                    VARCHAR(500),
    conveyance             VARCHAR(500),
    washing_allw           VARCHAR(500),
    other_allw             VARCHAR(500),
    gross_salary           VARCHAR(500),
    epf                    VARCHAR(500),
    esi                    VARCHAR(500),
    professional_tax       VARCHAR(500),
    labour_welfare_fund    VARCHAR(500),
    uniform_ded            VARCHAR(500),
    salary_advance         VARCHAR(500),
    total_deduct           VARCHAR(500),
    net_pay                VARCHAR(500),
    emp_signature          VARCHAR(500),
    upload_date          date
);


CREATE TABLE psfs.user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    login_id VARCHAR(100) UNIQUE NOT NULL,      -- Login identifier (can be email, etc.)
    name VARCHAR(200) NOT NULL,             -- Display name or full name
    password VARCHAR(255) NOT NULL,            -- Hashed password
    role VARCHAR(50) NOT NULL                  -- e.g., ROLE_USER, ROLE_ADMIN
);
