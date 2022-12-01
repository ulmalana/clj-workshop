// Compiled by ClojureScript 1.10.339 {:target :nodejs}
goog.provide('cljs.repl');
goog.require('cljs.core');
goog.require('cljs.spec.alpha');
cljs.repl.print_doc = (function cljs$repl$print_doc(p__3567){
var map__3568 = p__3567;
var map__3568__$1 = ((((!((map__3568 == null)))?(((((map__3568.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__3568.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__3568):map__3568);
var m = map__3568__$1;
var n = cljs.core.get.call(null,map__3568__$1,new cljs.core.Keyword(null,"ns","ns",441598760));
var nm = cljs.core.get.call(null,map__3568__$1,new cljs.core.Keyword(null,"name","name",1843675177));
cljs.core.println.call(null,"-------------------------");

cljs.core.println.call(null,[cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var temp__5457__auto__ = new cljs.core.Keyword(null,"ns","ns",441598760).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__5457__auto__)){
var ns = temp__5457__auto__;
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(ns),"/"].join('');
} else {
return null;
}
})()),cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m))].join(''));

if(cljs.core.truth_(new cljs.core.Keyword(null,"protocol","protocol",652470118).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Protocol");
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"forms","forms",2045992350).cljs$core$IFn$_invoke$arity$1(m))){
var seq__3570_3592 = cljs.core.seq.call(null,new cljs.core.Keyword(null,"forms","forms",2045992350).cljs$core$IFn$_invoke$arity$1(m));
var chunk__3571_3593 = null;
var count__3572_3594 = (0);
var i__3573_3595 = (0);
while(true){
if((i__3573_3595 < count__3572_3594)){
var f_3596 = cljs.core._nth.call(null,chunk__3571_3593,i__3573_3595);
cljs.core.println.call(null,"  ",f_3596);


var G__3597 = seq__3570_3592;
var G__3598 = chunk__3571_3593;
var G__3599 = count__3572_3594;
var G__3600 = (i__3573_3595 + (1));
seq__3570_3592 = G__3597;
chunk__3571_3593 = G__3598;
count__3572_3594 = G__3599;
i__3573_3595 = G__3600;
continue;
} else {
var temp__5457__auto___3601 = cljs.core.seq.call(null,seq__3570_3592);
if(temp__5457__auto___3601){
var seq__3570_3602__$1 = temp__5457__auto___3601;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__3570_3602__$1)){
var c__4351__auto___3603 = cljs.core.chunk_first.call(null,seq__3570_3602__$1);
var G__3604 = cljs.core.chunk_rest.call(null,seq__3570_3602__$1);
var G__3605 = c__4351__auto___3603;
var G__3606 = cljs.core.count.call(null,c__4351__auto___3603);
var G__3607 = (0);
seq__3570_3592 = G__3604;
chunk__3571_3593 = G__3605;
count__3572_3594 = G__3606;
i__3573_3595 = G__3607;
continue;
} else {
var f_3608 = cljs.core.first.call(null,seq__3570_3602__$1);
cljs.core.println.call(null,"  ",f_3608);


var G__3609 = cljs.core.next.call(null,seq__3570_3602__$1);
var G__3610 = null;
var G__3611 = (0);
var G__3612 = (0);
seq__3570_3592 = G__3609;
chunk__3571_3593 = G__3610;
count__3572_3594 = G__3611;
i__3573_3595 = G__3612;
continue;
}
} else {
}
}
break;
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m))){
var arglists_3613 = new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_((function (){var or__3949__auto__ = new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(or__3949__auto__)){
return or__3949__auto__;
} else {
return new cljs.core.Keyword(null,"repl-special-function","repl-special-function",1262603725).cljs$core$IFn$_invoke$arity$1(m);
}
})())){
cljs.core.prn.call(null,arglists_3613);
} else {
cljs.core.prn.call(null,((cljs.core._EQ_.call(null,new cljs.core.Symbol(null,"quote","quote",1377916282,null),cljs.core.first.call(null,arglists_3613)))?cljs.core.second.call(null,arglists_3613):arglists_3613));
}
} else {
}
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"special-form","special-form",-1326536374).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Special Form");

cljs.core.println.call(null," ",new cljs.core.Keyword(null,"doc","doc",1913296891).cljs$core$IFn$_invoke$arity$1(m));

if(cljs.core.contains_QMARK_.call(null,m,new cljs.core.Keyword(null,"url","url",276297046))){
if(cljs.core.truth_(new cljs.core.Keyword(null,"url","url",276297046).cljs$core$IFn$_invoke$arity$1(m))){
return cljs.core.println.call(null,["\n  Please see http://clojure.org/",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"url","url",276297046).cljs$core$IFn$_invoke$arity$1(m))].join(''));
} else {
return null;
}
} else {
return cljs.core.println.call(null,["\n  Please see http://clojure.org/special_forms#",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m))].join(''));
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Macro");
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"repl-special-function","repl-special-function",1262603725).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"REPL Special Function");
} else {
}

cljs.core.println.call(null," ",new cljs.core.Keyword(null,"doc","doc",1913296891).cljs$core$IFn$_invoke$arity$1(m));

if(cljs.core.truth_(new cljs.core.Keyword(null,"protocol","protocol",652470118).cljs$core$IFn$_invoke$arity$1(m))){
var seq__3574_3614 = cljs.core.seq.call(null,new cljs.core.Keyword(null,"methods","methods",453930866).cljs$core$IFn$_invoke$arity$1(m));
var chunk__3575_3615 = null;
var count__3576_3616 = (0);
var i__3577_3617 = (0);
while(true){
if((i__3577_3617 < count__3576_3616)){
var vec__3578_3618 = cljs.core._nth.call(null,chunk__3575_3615,i__3577_3617);
var name_3619 = cljs.core.nth.call(null,vec__3578_3618,(0),null);
var map__3581_3620 = cljs.core.nth.call(null,vec__3578_3618,(1),null);
var map__3581_3621__$1 = ((((!((map__3581_3620 == null)))?(((((map__3581_3620.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__3581_3620.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__3581_3620):map__3581_3620);
var doc_3622 = cljs.core.get.call(null,map__3581_3621__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists_3623 = cljs.core.get.call(null,map__3581_3621__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println.call(null);

cljs.core.println.call(null," ",name_3619);

cljs.core.println.call(null," ",arglists_3623);

if(cljs.core.truth_(doc_3622)){
cljs.core.println.call(null," ",doc_3622);
} else {
}


var G__3624 = seq__3574_3614;
var G__3625 = chunk__3575_3615;
var G__3626 = count__3576_3616;
var G__3627 = (i__3577_3617 + (1));
seq__3574_3614 = G__3624;
chunk__3575_3615 = G__3625;
count__3576_3616 = G__3626;
i__3577_3617 = G__3627;
continue;
} else {
var temp__5457__auto___3628 = cljs.core.seq.call(null,seq__3574_3614);
if(temp__5457__auto___3628){
var seq__3574_3629__$1 = temp__5457__auto___3628;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__3574_3629__$1)){
var c__4351__auto___3630 = cljs.core.chunk_first.call(null,seq__3574_3629__$1);
var G__3631 = cljs.core.chunk_rest.call(null,seq__3574_3629__$1);
var G__3632 = c__4351__auto___3630;
var G__3633 = cljs.core.count.call(null,c__4351__auto___3630);
var G__3634 = (0);
seq__3574_3614 = G__3631;
chunk__3575_3615 = G__3632;
count__3576_3616 = G__3633;
i__3577_3617 = G__3634;
continue;
} else {
var vec__3583_3635 = cljs.core.first.call(null,seq__3574_3629__$1);
var name_3636 = cljs.core.nth.call(null,vec__3583_3635,(0),null);
var map__3586_3637 = cljs.core.nth.call(null,vec__3583_3635,(1),null);
var map__3586_3638__$1 = ((((!((map__3586_3637 == null)))?(((((map__3586_3637.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__3586_3637.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__3586_3637):map__3586_3637);
var doc_3639 = cljs.core.get.call(null,map__3586_3638__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists_3640 = cljs.core.get.call(null,map__3586_3638__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println.call(null);

cljs.core.println.call(null," ",name_3636);

cljs.core.println.call(null," ",arglists_3640);

if(cljs.core.truth_(doc_3639)){
cljs.core.println.call(null," ",doc_3639);
} else {
}


var G__3641 = cljs.core.next.call(null,seq__3574_3629__$1);
var G__3642 = null;
var G__3643 = (0);
var G__3644 = (0);
seq__3574_3614 = G__3641;
chunk__3575_3615 = G__3642;
count__3576_3616 = G__3643;
i__3577_3617 = G__3644;
continue;
}
} else {
}
}
break;
}
} else {
}

if(cljs.core.truth_(n)){
var temp__5457__auto__ = cljs.spec.alpha.get_spec.call(null,cljs.core.symbol.call(null,[cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.ns_name.call(null,n))].join(''),cljs.core.name.call(null,nm)));
if(cljs.core.truth_(temp__5457__auto__)){
var fnspec = temp__5457__auto__;
cljs.core.print.call(null,"Spec");

var seq__3588 = cljs.core.seq.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"args","args",1315556576),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Keyword(null,"fn","fn",-1175266204)], null));
var chunk__3589 = null;
var count__3590 = (0);
var i__3591 = (0);
while(true){
if((i__3591 < count__3590)){
var role = cljs.core._nth.call(null,chunk__3589,i__3591);
var temp__5457__auto___3645__$1 = cljs.core.get.call(null,fnspec,role);
if(cljs.core.truth_(temp__5457__auto___3645__$1)){
var spec_3646 = temp__5457__auto___3645__$1;
cljs.core.print.call(null,["\n ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name.call(null,role)),":"].join(''),cljs.spec.alpha.describe.call(null,spec_3646));
} else {
}


var G__3647 = seq__3588;
var G__3648 = chunk__3589;
var G__3649 = count__3590;
var G__3650 = (i__3591 + (1));
seq__3588 = G__3647;
chunk__3589 = G__3648;
count__3590 = G__3649;
i__3591 = G__3650;
continue;
} else {
var temp__5457__auto____$1 = cljs.core.seq.call(null,seq__3588);
if(temp__5457__auto____$1){
var seq__3588__$1 = temp__5457__auto____$1;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__3588__$1)){
var c__4351__auto__ = cljs.core.chunk_first.call(null,seq__3588__$1);
var G__3651 = cljs.core.chunk_rest.call(null,seq__3588__$1);
var G__3652 = c__4351__auto__;
var G__3653 = cljs.core.count.call(null,c__4351__auto__);
var G__3654 = (0);
seq__3588 = G__3651;
chunk__3589 = G__3652;
count__3590 = G__3653;
i__3591 = G__3654;
continue;
} else {
var role = cljs.core.first.call(null,seq__3588__$1);
var temp__5457__auto___3655__$2 = cljs.core.get.call(null,fnspec,role);
if(cljs.core.truth_(temp__5457__auto___3655__$2)){
var spec_3656 = temp__5457__auto___3655__$2;
cljs.core.print.call(null,["\n ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name.call(null,role)),":"].join(''),cljs.spec.alpha.describe.call(null,spec_3656));
} else {
}


var G__3657 = cljs.core.next.call(null,seq__3588__$1);
var G__3658 = null;
var G__3659 = (0);
var G__3660 = (0);
seq__3588 = G__3657;
chunk__3589 = G__3658;
count__3590 = G__3659;
i__3591 = G__3660;
continue;
}
} else {
return null;
}
}
break;
}
} else {
return null;
}
} else {
return null;
}
}
});

//# sourceMappingURL=repl.js.map
