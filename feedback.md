# mp3 Feedback

## Grade: 1.5

| Compilation | Timeout | Duration |
|:-----------:|:-------:|:--------:|
|Yes|No|53.82|

## Test Results
### cpen221.mp3.Task1Grader
| Test Status | Count |
| ----------- | ----- |
|skipped|0|
|failures|2|
|tests|10|
|errors|0|
#### Failed Tests
1. `testBufferEviction (java.lang.AssertionError: FSFTBuffer: No exception expected)`
1. `testUpdate (java.lang.AssertionError: expected:<cpen221.mp3.Text@54bb10d0> but was:<cpen221.mp3.Text@73f1ee3>)`
### cpen221.mp3.Task2Grader
| Test Status | Count |
| ----------- | ----- |
|skipped|0|
|failures|1|
|tests|4|
|errors|0|
#### Failed Tests
1. `testUpdate (java.lang.AssertionError: FSFTBuffer: concurrent update failure)`

## Comments


## Test Coverage
### FSFTBuffer
| Metric | Coverage |
| ------ | -------- |
|LINE_MISSED|4|
|LINE_COVERED|53|
|BRANCH_COVERED|18|
|BRANCH_MISSED|2|

## Checkstyle Results
### `FSFTBuffer.java`
| Line | Column | Message |
| ---- | ------ | ------- |
| 1 | None | `File does not end with a newline.` |
| 27 | None | `Type Javadoc comment is missing @author tag.` |
| 66 | None | `Expected @return tag.` |
| 66 | 39 | `Expected @param tag for 't'.` |
| 78 | 33 | `'+' is not followed by whitespace.` |
| 78 | 33 | `'+' is not preceded with whitespace.` |
| 119 | 62 | `'/' is not followed by whitespace.` |
| 119 | 62 | `'/' is not preceded with whitespace.` |
| 1 | None | `File does not end with a newline.` |
| 27 | None | `Type Javadoc comment is missing @author tag.` |
| 66 | None | `Expected @return tag.` |
| 66 | 39 | `Expected @param tag for 't'.` |
| 78 | 33 | `'+' is not followed by whitespace.` |
| 78 | 33 | `'+' is not preceded with whitespace.` |
| 119 | 62 | `'/' is not followed by whitespace.` |
| 119 | 62 | `'/' is not preceded with whitespace.` |

