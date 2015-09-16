# fads

Clojure library providing easier access to Facebook's Ads API.

## Usage

```clojure
(use 'fads.core)
(use 'clj-time.core)

(def token "FACEBOOK_OAUTH2_TOKEN")
(def account-id "FACEBOOK_ADS_ACCOUNT_ID")

(insights token account-id {:since (from-now (days -7))
                            :until (today)}))
# ({:date_start "2015-01-01", :campaign_name ...})
```

## License

Copyright © 2015 uSwitch

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
