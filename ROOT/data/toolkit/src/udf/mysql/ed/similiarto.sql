-- $Id: edth.sql 4068 2008-10-14 01:56:51Z rares $
--
-- Copyright (C) 2008 by The Regents of the University of California
--
-- Redistribution of this file is permitted under the terms of the 
-- BSD license
--
-- Date: 05/15/2008
-- Author: Rares Vernica <rares (at) ics.uci.edu>

DROP FUNCTION IF EXISTS SIMILIARTO;
CREATE FUNCTION SIMILIARTO RETURNS INTEGER SONAME 'libsimiliarto.so';

SELECT SIMILIARTO('abc', 'ad', 1);
SELECT SIMILIARTO('abc', 'ad', 2);
SELECT SIMILIARTO('abc', 'aaa', 1);
SELECT SIMILIARTO('abc', 'aaa', 2);
SELECT SIMILIARTO('abc', 'abcd', 1);
SELECT SIMILIARTO('abc', 'abcd', 2);
SELECT SIMILIARTO('a', 'abcdefghijklmnopqrstuvwxyz', 2);
