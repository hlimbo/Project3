#!/bin/sh
echo "singleclustertj"
python3 avgLog.py results/singleclustertj.txt
echo "singleclusterts"
python3 avgLog.py results/singleclusterts.txt

echo "singlesingletj"
python3 avgLog.py results/singlesingletj.txt
echo "singlesinglets"
python3 avgLog.py results/singlesinglets.txt

echo "tenclusterconnectionlesstj"
python3 avgLog.py results/tenclusterconnectionlesstj.txt
echo "tenclusterconnectionlessts"
python3 avgLog.py results/tenclusterconnectionlessts.txt

echo "tenclustertj"
python3 avgLog.py results/tenclustertj.txt
echo "tenclusterts"
python3 avgLog.py results/tenclusterts.txt

echo "tenclusterstatementstj"
python3 avgLog.py results/tenclusterstatementstj.txt
echo "tenclusterstatementsts"
python3 avgLog.py results/tenclusterstatementsts.txt

echo "tenhttpstj"
python3 avgLog.py results/tenhttpstj.txt
echo "tenhttpsts"
python3 avgLog.py results/tenhttpsts.txt

echo "tensingleconnectionlesstj"
python3 avgLog.py results/tensingleconnectionlesstj.txt
echo "tensingleconnectionlessts"
python3 avgLog.py results/tensingleconnectionlessts.txt

echo "tensingletj"
python3 avgLog.py results/tensingletj.txt
echo "tensinglets"
python3 avgLog.py results/tensinglets.txt

echo "tensinglestatementstj"
python3 avgLog.py results/tensinglestatementstj.txt
echo "tensinglestatementsts"
python3 avgLog.py results/tensinglestatementsts.txt
