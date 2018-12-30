# Poisson Optimizer and Simulator

Using poisson distribution to optimize/simulate staffing count decisions to minimize wait times. 

## Abstract

You need to decide how many bank tellers `x` to staff for a given work shift of period `t_shift`. 

During this period, there is an average of `c` customers arriving on average during this `t_shift` period. There is also an average processing time `t_processing` for each customer. 

Your objective is to ensure there is no more than a 5% chance a customer waits more than 5 minutes `t_wait`. Achieve this using a combination of optimization or simulation.
