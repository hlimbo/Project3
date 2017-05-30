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

SELECT SIMILIARTO('abc', 'xxx abcd yyy', 1);
SELECT SIMILIARTO('abc', 'xxx abcd, yyy', 1);
SELECT SIMILIARTO('abc', 'xxx abcd. yyy', 1);
SELECT SIMILIARTO('abc', 'xxx ,.abcd,. yyy', 1);

SELECT SIMILIARTO('abc', 'xxx Abcd yyy', 1);
SELECT SIMILIARTO('abc', 'xxx ABCD yyy', 1);

SELECT SIMILIARTO('xy yx ab', 'xx ad xx', 1);
