selectFileByWarnId
===
  ```sql
    select file_id as fileId from relocation_file
    where warn_id = #{warnId}
  ```