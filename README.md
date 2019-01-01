# Poisson Optimizer and Simulator

Using poisson distribution to simulate staffing headcount decisions and impact on wait times.

## Abstract

This is a Poisson simulator that accepts probabilistic parameters (number of staff, average customer arrivals per hour, average minutes to process a customer) and animates the outcome over a specified period.

The animation was built with JavaFX/TornadoFX. 

![](animation.gif)

**Sample Output:**

```
minute=1 arriving= serving= waiting=
minute=2 arriving=0[7] serving=0[7] waiting=
minute=3 arriving=1[10],2[3] serving=0[7],1[10],2[3] waiting=
minute=4 arriving= serving=0[7],1[10],2[3] waiting=
minute=5 arriving=3[2] serving=0[7],1[10],2[3] waiting=3[2]
minute=6 arriving= serving=0[7],1[10],3[2] waiting=
minute=7 arriving=4[2],5[6] serving=0[7],1[10],3[2] waiting=4[2],5[6]
minute=8 arriving= serving=0[7],1[10],4[2] waiting=5[6]
minute=9 arriving= serving=1[10],4[2],5[6] waiting=
minute=10 arriving= serving=1[10],5[6] waiting=
minute=11 arriving= serving=1[10],5[6] waiting=
minute=12 arriving= serving=1[10],5[6] waiting=
minute=13 arriving=6[7] serving=5[6],6[7] waiting=
minute=14 arriving= serving=5[6],6[7] waiting=
minute=15 arriving= serving=6[7] waiting=
minute=16 arriving= serving=6[7] waiting=
minute=17 arriving=7[5] serving=6[7],7[5] waiting=
minute=18 arriving= serving=6[7],7[5] waiting=
minute=19 arriving=8[10] serving=6[7],7[5],8[10] waiting=
minute=20 arriving=9[2] serving=7[5],8[10],9[2] waiting=
minute=21 arriving= serving=7[5],8[10],9[2] waiting=
minute=22 arriving= serving=8[10] waiting=
minute=23 arriving=10[3] serving=8[10],10[3] waiting=
minute=24 arriving=11[4] serving=8[10],10[3],11[4] waiting=
minute=25 arriving=12[3] serving=8[10],10[3],11[4] waiting=12[3]
minute=26 arriving=13[4] serving=8[10],11[4],12[3] waiting=13[4]
minute=27 arriving= serving=8[10],11[4],12[3] waiting=13[4]
minute=28 arriving= serving=8[10],12[3],13[4] waiting=
minute=29 arriving= serving=13[4] waiting=
minute=30 arriving=14[6],15[6] serving=13[4],14[6],15[6] waiting=
minute=31 arriving=16[3] serving=13[4],14[6],15[6] waiting=16[3]
minute=32 arriving=17[6] serving=14[6],15[6],16[3] waiting=17[6]
minute=33 arriving= serving=14[6],15[6],16[3] waiting=17[6]
minute=34 arriving= serving=14[6],15[6],16[3] waiting=17[6]
minute=35 arriving= serving=14[6],15[6],17[6] waiting=
minute=36 arriving= serving=17[6] waiting=
minute=37 arriving=18[8] serving=17[6],18[8] waiting=
minute=38 arriving=19[3],20[4] serving=17[6],18[8],19[3] waiting=20[4]
minute=39 arriving= serving=17[6],18[8],19[3] waiting=20[4]
minute=40 arriving=21[7] serving=17[6],18[8],19[3] waiting=20[4],21[7]
minute=41 arriving=22[12] serving=18[8],20[4],21[7] waiting=22[12]
minute=42 arriving=23[6] serving=18[8],20[4],21[7] waiting=22[12],23[6]
minute=43 arriving= serving=18[8],20[4],21[7] waiting=22[12],23[6]
minute=44 arriving=24[10] serving=18[8],20[4],21[7] waiting=22[12],23[6],24[10]
minute=45 arriving= serving=21[7],22[12],23[6] waiting=24[10]
minute=46 arriving= serving=21[7],22[12],23[6] waiting=24[10]
minute=47 arriving= serving=21[7],22[12],23[6] waiting=24[10]
minute=48 arriving= serving=22[12],23[6],24[10] waiting=
minute=49 arriving= serving=22[12],23[6],24[10] waiting=
minute=50 arriving= serving=22[12],23[6],24[10] waiting=
minute=51 arriving= serving=22[12],24[10] waiting=
minute=52 arriving= serving=22[12],24[10] waiting=
minute=53 arriving= serving=22[12],24[10] waiting=
minute=54 arriving=25[6],26[11] serving=22[12],24[10],25[6] waiting=26[11]
minute=55 arriving= serving=22[12],24[10],25[6] waiting=26[11]
minute=56 arriving= serving=22[12],24[10],25[6] waiting=26[11]
minute=57 arriving=27[7] serving=24[10],25[6],26[11] waiting=27[7]
minute=58 arriving= serving=25[6],26[11],27[7] waiting=
minute=59 arriving= serving=25[6],26[11],27[7] waiting=
minute=60 arriving= serving=26[11],27[7] waiting=
minute=61 arriving= serving=26[11],27[7] waiting=
minute=62 arriving=28[4] serving=26[11],27[7],28[4] waiting=
minute=63 arriving= serving=26[11],27[7],28[4] waiting=
minute=64 arriving= serving=26[11],27[7],28[4] waiting=
minute=65 arriving= serving=26[11],28[4] waiting=
minute=66 arriving= serving=26[11] waiting=
minute=67 arriving= serving=26[11] waiting=
minute=68 arriving= serving= waiting=
minute=69 arriving=29[2],30[8] serving=29[2],30[8] waiting=
minute=70 arriving= serving=29[2],30[8] waiting=
minute=71 arriving=31[9],32[4] serving=30[8],31[9],32[4] waiting=
minute=72 arriving=33[2] serving=30[8],31[9],32[4] waiting=33[2]
minute=73 arriving=34[1] serving=30[8],31[9],32[4] waiting=33[2],34[1]
minute=74 arriving=35[5] serving=30[8],31[9],32[4] waiting=33[2],34[1],35[5]
minute=75 arriving= serving=30[8],31[9],33[2] waiting=34[1],35[5]
minute=76 arriving= serving=30[8],31[9],33[2] waiting=34[1],35[5]
minute=77 arriving= serving=31[9],34[1],35[5] waiting=
minute=78 arriving= serving=31[9],35[5] waiting=
minute=79 arriving= serving=31[9],35[5] waiting=
minute=80 arriving=36[2] serving=35[5],36[2] waiting=
minute=81 arriving=37[6] serving=35[5],36[2],37[6] waiting=
minute=82 arriving= serving=37[6] waiting=
minute=83 arriving= serving=37[6] waiting=
minute=84 arriving=38[9] serving=37[6],38[9] waiting=
minute=85 arriving=39[7] serving=37[6],38[9],39[7] waiting=
minute=86 arriving=40[6] serving=37[6],38[9],39[7] waiting=40[6]
minute=87 arriving= serving=38[9],39[7],40[6] waiting=
minute=88 arriving=41[4],42[5] serving=38[9],39[7],40[6] waiting=41[4],42[5]
minute=89 arriving= serving=38[9],39[7],40[6] waiting=41[4],42[5]
minute=90 arriving= serving=38[9],39[7],40[6] waiting=41[4],42[5]
minute=91 arriving=43[4] serving=38[9],39[7],40[6] waiting=41[4],42[5],43[4]
minute=92 arriving= serving=38[9],40[6],41[4] waiting=42[5],43[4]
minute=93 arriving= serving=41[4],42[5],43[4] waiting=
minute=94 arriving=44[4] serving=41[4],42[5],43[4] waiting=44[4]
minute=95 arriving= serving=41[4],42[5],43[4] waiting=44[4]
minute=96 arriving=45[9] serving=42[5],43[4],44[4] waiting=45[9]
minute=97 arriving=46[9] serving=42[5],44[4],45[9] waiting=46[9]
minute=98 arriving= serving=44[4],45[9],46[9] waiting=
minute=99 arriving= serving=44[4],45[9],46[9] waiting=
minute=100 arriving= serving=45[9],46[9] waiting=
minute=101 arriving= serving=45[9],46[9] waiting=
minute=102 arriving= serving=45[9],46[9] waiting=
minute=103 arriving=47[3] serving=45[9],46[9],47[3] waiting=
minute=104 arriving= serving=45[9],46[9],47[3] waiting=
minute=105 arriving= serving=45[9],46[9],47[3] waiting=
minute=106 arriving=48[5] serving=46[9],48[5] waiting=
minute=107 arriving= serving=48[5] waiting=
minute=108 arriving=49[3] serving=48[5],49[3] waiting=
minute=109 arriving= serving=48[5],49[3] waiting=
minute=110 arriving= serving=48[5],49[3] waiting=
minute=111 arriving=50[7] serving=50[7] waiting=
minute=112 arriving=51[3] serving=50[7],51[3] waiting=
minute=113 arriving=52[4] serving=50[7],51[3],52[4] waiting=
minute=114 arriving=53[6] serving=50[7],51[3],52[4] waiting=53[6]
minute=115 arriving=54[3] serving=50[7],52[4],53[6] waiting=54[3]
minute=116 arriving=55[4] serving=50[7],52[4],53[6] waiting=54[3],55[4]
minute=117 arriving=56[3] serving=50[7],53[6],54[3] waiting=55[4],56[3]
minute=118 arriving= serving=53[6],54[3],55[4] waiting=56[3]
minute=119 arriving= serving=53[6],54[3],55[4] waiting=56[3]
minute=120 arriving= serving=53[6],55[4],56[3] waiting=
```
