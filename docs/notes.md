# Notes 

## Todo: 
- [ ] Would utilizing integers for the primary keys be more efficent than the strings? (i.e. swapping ea. String ID attribute to a SMALLIN or INT value in the DB).
- [X] Clarify difference between the process.log and the debug.log more clearly & make sure that there are no opinions omittting key information from being logged, or otherwise to the wrong log. 
- [X] The newer Original Dataset import-dataset command, each of the resources workds when imported individually, but there are some stinky behaviors digging in. When run all at once the .jsonl hyperlinks table is being build but not inserted into the database. Additionally the performance seems to have been impacted pretty significantly. Somewhere in the refactor I made the code worse - may make sense to revert to each refactor individually and iterate and test more step by steppedly. 