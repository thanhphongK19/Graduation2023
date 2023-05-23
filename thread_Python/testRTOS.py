
import pyRTOS

def task1(self):
    a = 0
    yield
    while True:
        print('task 1 is running',a)
        a = a + 1
        yield[pyRTOS.timeout(5)]

def task2(self):
    b = 0
    yield
    while True:
        print('task 2 is running',b)
        b = b + 1
        yield[pyRTOS.timeout(10)]



pyRTOS.pyRTOS.add_task(pyRTOS.Task(task1,priority=1))
pyRTOS.add_task(pyRTOS.Task(task2,priority=2))

pyRTOS.start()