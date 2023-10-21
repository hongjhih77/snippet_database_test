## A snippet for testing databases on a bug occurred before when using MySql_5.7

### Stack

JAVA, testcontainers

### The context of the bug

![write_skew.png](write_skew.png)

#### How to fix it?
Change Sept 8. to
```SQL
UPDATE bill SET is_sync = 1  WHERE bill_id in (A,B);
```

### Result

Expect the return is **2**.

| database     | isolation       | return                         |
|--------------|-----------------|--------------------------------|
| MySQL 5.7, 8 | REPEATABLE_READ | 3                              |
| MySQL 5.7, 8 | SERIALIZABLE    | "Lock wait timeout" on step 5. |
| Postgres 9   | REPEATABLE_READ | 2                              |
| Postgres 9   | READ_COMMITTED  | 3                              |
