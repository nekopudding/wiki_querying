# mp3 Feedback

## Grade: 1.5

| Compilation | Timeout | Duration |
|:-----------:|:-------:|:--------:|
|Yes|No|50.5|

## Test Results
### cpen221.mp3.Task2Grader
| Test Status | Count |
| ----------- | ----- |
|tests|4|
|errors|0|
|skipped|0|
|failures|1|
#### Failed Tests
1. `testUpdate (java.lang.AssertionError: FSFTBuffer: concurrent update failure)`
### cpen221.mp3.Task1Grader
| Test Status | Count |
| ----------- | ----- |
|tests|10|
|errors|0|
|skipped|0|
|failures|4|
#### Failed Tests
1. `testTouch (java.lang.AssertionError: FSFTBuffer: Item should not have timed out)`
1. `testBufferEviction (java.lang.AssertionError: FSFTBuffer: No exception expected)`
1. `testCapacityEvictionAndTimeout (java.lang.AssertionError: FSFTBuffer: item should have timed out)`
1. `testUpdate (java.lang.AssertionError: expected:<cpen221.mp3.Text@5f5fc8a6> but was:<cpen221.mp3.Text@118977a6>)`

## Comments

FSFTBuffer.java:35:	Found non-transient, non-static member. Please mark as transient or provide accessors.

FSFTBuffer.java:36:	Found non-transient, non-static member. Please mark as transient or provide accessors.

FSFTBuffer.java:37:	Found non-transient, non-static member. Please mark as transient or provide accessors.

FSFTBuffer.java:38:	Found non-transient, non-static member. Please mark as transient or provide accessors.

FSFTBuffer.java:70:	Potential violation of Law of Demeter (object not created locally)

FSFTBuffer.java:71:	System.out.println is used

FSFTBuffer.java:78:	System.out.println is used

FSFTBuffer.java:91:	Found 'DD'-anomaly for variable 'stalestObj' (lines '91'-'97').

FSFTBuffer.java:92:	Found 'DU'-anomaly for variable 'minTimeout' (lines '92'-'101').

FSFTBuffer.java:96:	Found 'DU'-anomaly for variable 'minTimeout' (lines '96'-'101').

FSFTBuffer.java:97:	Found 'DD'-anomaly for variable 'stalestObj' (lines '97'-'97').

FSFTBuffer.java:121:	System.out.println is used

FSFTBuffer.java:139:	System.out.println is used

FSFTBuffer.java:157:	Avoid unnecessary if..then..else statements when returning booleans

LoosePackageCoupling	-	No packages or classes specified

## Test Coverage
### FSFTBuffer
| Metric | Coverage |
| ------ | -------- |
|BRANCH_COVERED|16|
|BRANCH_MISSED|4|
|LINE_COVERED|53|
|LINE_MISSED|4|

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

