# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 1/5 (20.0%)
- **Function parity:** 2/9 matched (target 10) — 22.2%
- **Class/type parity:** 0/1 matched (target 2) — 0.0%
- **Combined symbol parity:** 2/10 matched (target 12) — 20.0%
- **Average inline-code cosine:** 0.49 (function body across 1 matched files)
- **Average documentation cosine:** 0.00 (doc text across 1 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 1 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. lib

- **Target:** `pathabsolutize.PathAbsolutize`
- **Similarity:** 0.49
- **Dependents:** 0
- **Priority Score:** 10305.1
- **Functions:** 2/3 matched (target 10)
- **Missing functions:** `absolutize`
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

