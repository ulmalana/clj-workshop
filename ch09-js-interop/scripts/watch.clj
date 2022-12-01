(require '[cljs.build.api :as b])

(b/watch "src"
  {:main 'ch09-js-interop.core
   :output-to "out/ch09_js_interop.js"
   :output-dir "out"})
