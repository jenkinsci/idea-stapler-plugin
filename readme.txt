Current problem.

 - AST doesn't seem to be rebuilt, so we end up having stale references.
   Who's supposed to trigger the AST reconstruction?
        -> Added ChangeVisitorImpl and that fixed it.

 - Ctrl+W doesn't work on attributes.
    presumably         SelectWordUtil.registerSelectioner(...);
    Defining intermediate PSIs (like JellyAttribute) made it work


 - Auto-completion needs to work

 - xmlns decls are marked as errors