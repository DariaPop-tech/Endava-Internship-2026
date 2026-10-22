# BUG-001 - DELETE /contacts/{id} removes all contacts for an unknown id

| Field | Value |
|---|---|
| Status | confirmed - live reproduction verified twice |
| Severity | major - all contact relationships are removed, consistent with KI-01 |
| Priority | P1 - high-severity data-integrity behavior, consistent with KI-01 |
| Area | `DELETE /contacts/:contactId` |
| Reported by / date | manual-tester / 2026-10-20 |
| Related | KI-01 in `tap26/knowledge/known-issues.md` |
| Reproduction rate | 2/2 |

## Environment
- Commit: unavailable - Branch: unavailable
- API: http://localhost:3001 - OS: Windows - Node: v22.20.0
- User: Heath93 (seeded user; logged in for each run)
- Database: 15 contact records before each run; restored to seed data after the two runs

## Preconditions
- The local API was running in development or test mode, and the database was in seed state with 15 contact records.
- The test-data endpoints used below are available only in development or test mode.

## Steps to reproduce
1. Start with the seeded database containing 15 contact records.
2. In PowerShell, create an authenticated session and run the first reproduction:

	```powershell
	$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
	$loginBody = @{ username = "Heath93"; password = "s3cret" } | ConvertTo-Json
	$null = Invoke-RestMethod -Uri "http://localhost:3001/login" -Method Post -ContentType "application/json" -Body $loginBody -WebSession $session
	$before = (Invoke-RestMethod -Uri "http://localhost:3001/testData/contacts").results.Count
	$delete = Invoke-WebRequest -Uri "http://localhost:3001/contacts/unknown-b92717c3a0fd" -Method Delete -WebSession $session -UseBasicParsing
	$after = (Invoke-RestMethod -Uri "http://localhost:3001/testData/contacts").results.Count
	"HTTP $($delete.StatusCode); contacts: $before -> $after"
	```

3. Observe `HTTP 200; contacts: 15 -> 0`.
4. To repeat, restore the seed and create a new session. **This reset replaces all local database data.** Then run the same commands with `unknown-c87b48fe476d` as the ID:

	```powershell
	$null = Invoke-RestMethod -Uri "http://localhost:3001/testData/seed" -Method Post
	$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
	$null = Invoke-RestMethod -Uri "http://localhost:3001/login" -Method Post -ContentType "application/json" -Body $loginBody -WebSession $session
	$before = (Invoke-RestMethod -Uri "http://localhost:3001/testData/contacts").results.Count
	$delete = Invoke-WebRequest -Uri "http://localhost:3001/contacts/unknown-c87b48fe476d" -Method Delete -WebSession $session -UseBasicParsing
	$after = (Invoke-RestMethod -Uri "http://localhost:3001/testData/contacts").results.Count
	"HTTP $($delete.StatusCode); contacts: $before -> $after"
	```

5. Observe `HTTP 200; contacts: 15 -> 0` again. Restore the seed afterward if you want the local database back to its original state; this also replaces all local database data.

## Expected result
Deleting a well-formed but unknown contact id should not remove any existing contact records. The expected HTTP status for an unknown id is not specified here as an OpenAPI requirement.

## Actual result
Both DELETE requests returned HTTP 200. In each run, all 15 contact records were removed and the resulting contact count was 0. This confirms the existing KI-01 behavior; it is not a new defect report.

## Evidence
```http
DELETE /contacts/unknown-b92717c3a0fd

-> 200
Contacts: 15 before, 0 after

DELETE /contacts/unknown-c87b48fe476d

-> 200
Contacts: 15 before, 0 after
```
- Reproduction was performed twice using seeded user `Heath93`; the database was restored to seed data afterward.
- Screenshot / trace: not applicable (API-only procedure; not run).
- `data-test` elements involved: not applicable.

## Impact
An authenticated caller can remove all contact relationships by deleting a well-formed id that does not exist. This is the impact already documented by KI-01.

## Regression test
- `playwright/tests/api/social.api.spec.ts` › `DELETE /contacts/:contactId does not alter contacts for an unknown id` — asserts contacts remain unchanged and is marked `test.fail(true, "KI-01: unknown contact id must not delete contacts")` while KI-01 remains. The automated test still needs to be run twice and typechecked.

## Notes
- Commit and branch metadata were unavailable.
- This report records confirmed evidence for KI-01 only; it does not introduce a new issue.