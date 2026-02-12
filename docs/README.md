# Incident Tracker — Quick Guide

Purpose
- Store basic incident data and optional reporter info that can be enriched from an external User Service.

Important fields
- reporterName — reporter display name
- reporterEmail — reporter email

How to use
- You can set reporterName/reporterEmail directly when creating an Incident.
- Optionally, enrich these fields from a User Service before saving.

Notes
- assignedDeveloper currently stores a simple name or ID.
- Enrichment is not implemented here; this file documents intent only.

Next steps
- Implement a UserService client (HTTP/gRPC) in the service layer to fetch user details.
- Populate reporterName and reporterEmail from that client before persisting incidents.
- Add integration tests to cover enrichment flows.

Build
- Recompile the project after updating code or adding the enrichment service.
