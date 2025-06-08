(defproject org.clojars.pkoerner/lisb-rodin-plugin-adapter "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :deploy-repositories [["releases"  {:sign-releases false :url "https://repo.clojars.org/"}]
                        ["snapshots" {:sign-releases false :url "https://repo.clojars.org/"}]]
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojars.pkoerner/lisb "0.0.6-SNAPSHOT"]
                 ]
  :aot [lisb-rodin-plugin-adapter.core]
  :repl-options {:init-ns lisb-rodin-plugin-adapter.core})
