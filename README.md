# football-pool
A Pick'em Football Pool written in Java using [Vaadin 7](https://vaadin.com/home).  This was used to run a pool site hosted on [Google App Engine](https://cloud.google.com/appengine/docs) for several years.  Everything generally works well but there are a couple areas that could be improved.

## TODO
- There are GAE caching issues where game results are either not updated or revert to old values.

- Playoffs aren't handled great.  Assigning the playoff teams and playoff "week" requires code changes.

- There are a couple debugging endpoints that only use security by obscurity.

- See TODOs in the code for additional setup that is required for SSO registration, etc.
