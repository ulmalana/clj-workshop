// Compiled by ClojureScript 1.10.339 {:target :nodejs}
goog.provide('ch09_js_interop.core');
goog.require('cljs.core');
goog.require('clojure.browser.repl');
ch09_js_interop.core.test_fun = (function ch09_js_interop$core$test_fun(){
return cljs.core.println.call(null,"ini percobaan fungsi");
});
ch09_js_interop.core.languages = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"Clojure","Clojure",-1201964559),"CLJ",new cljs.core.Keyword(null,"ClojureScript","ClojureScript",621012456),"CLJS",new cljs.core.Keyword(null,"JavaScript","JavaScript",-408888648),"JS"], null);
ch09_js_interop.core.language_abbreviator = (function ch09_js_interop$core$language_abbreviator(language){
var temp__5455__auto__ = cljs.core.get.call(null,ch09_js_interop.core.languages,language);
if(cljs.core.truth_(temp__5455__auto__)){
var lang = temp__5455__auto__;
return lang;
} else {
throw (new Error("Language not supported"));
}
});
ch09_js_interop.core.get_language_of_the_week = (function ch09_js_interop$core$get_language_of_the_week(languages){
var lang_of_the_week = cljs.core.rand_nth.call(null,languages);
try{return ["Language of the week is: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(ch09_js_interop.core.language_abbreviator.call(null,lang_of_the_week))].join('');
}catch (e604){if((e604 instanceof Error)){
var e = e604;
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(lang_of_the_week)," is not supported"].join('');
} else {
throw e604;

}
}finally {cljs.core.println.call(null,lang_of_the_week,"was chosen as the language of the week");
}});
cljs.core.enable_console_print_BANG_.call(null);
cljs.core.println.call(null,"Hello world!");

//# sourceMappingURL=core.js.map
