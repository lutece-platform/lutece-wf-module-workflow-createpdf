--
-- Table structure for table tf_directory_cf
--
DROP TABLE IF EXISTS task_create_pdf_cf;
CREATE TABLE task_create_pdf_cf(
  id_task INT DEFAULT NULL,
  id_directory INT DEFAULT NULL,
  id_entry_url_pdf INT DEFAULT NULL,
  id_config INT DEFAULT NULL,
  PRIMARY KEY (id_task)
);