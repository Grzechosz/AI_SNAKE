import numpy as np
from time import sleep
import math
import copy


input_neurons = 192
hidden_neurons = 64
output_neurons = 4
agents_num = 300
the_best_agents_num = 20
loops = 5000

def init_layer(number_of_neurons, number_of_inputs_per_neuron):
    synaptic_weights = 2 * np.random.random((number_of_inputs_per_neuron, number_of_neurons)) - 1
    return synaptic_weights

def population_init():
    population = []
    for i in range(agents_num):
        layer_1 = init_layer(64,192)
        layer_2 = init_layer(4, 64)
        layers = {"W1": layer_1,"W2": layer_2,"eff": 0}
        population.append(layers)
    return population
    
def selection_model(population):
    population = sorted(population, key=lambda d: d['eff'],reverse=True)
    return population[:the_best_agents_num]
    
class GeneticAlgorithm:
    
    def __init__(self):
        self.first_generation_func = population_init
        self.selection_model = selection_model      
        self.mutation_probability = 0.1             
        self.crossover_probability = 0.7            

    def run(self):
        population = self.first_generation_func()
        j = 0
        for p in population:
            j+=1
            step = 0
            ttl = 0
            points = 0;
            prev_points = 0;
            gameover = False
            print("Osobnik nr: ", j)
            while not gameover and ttl<150:
                prev_points=points
                costam, gameover, points = player(p, step)
                if prev_points<points:
                    ttl=0
                # if points==0:
                #      p['eff']=0
                step+=1
                ttl+=1
            print("fitness: ", p['eff'])
        i = 1
        while i<=loops:
            j=0
            selected = self.selection_model(population)
            new_population = copy.copy(selected)
            print("Generacja: {} najlepszy fitness: {}".format(i, selected[0]['eff']))    
            for p in new_population:
                p['eff']=0    
            sleep(2)
            while len(new_population) != agents_num:
                mum = copy.deepcopy(np.random.choice(new_population))
                dad = copy.deepcopy(np.random.choice(new_population))
                child = mum
                child["eff"] = 0
                if np.random.random() <= self.crossover_probability:
                    for x in range(hidden_neurons):
                        for y in range(output_neurons):
                            if y%2==0:
                                child['W1'][x][y] = dad['W1'][x][y]
                            else:
                                child['W2'][x][y] = dad['W1'][x][y]

                if np.random.random() <= self.mutation_probability:
                    if np.random.random()<0.5:
                        child['W1'][x][y]=2*np.random.rand()-1
                    else:
                        child['W2'][x][y]=2*np.random.rand()-1
                new_population.append(child)
            population = new_population;
            i += 1
            for p in population:
                j+=1
                print("Osobnik nr: ", j)
                step = 0
                ttl = 0
                points = 0;
                prev_points = 0;
                gameover = False
                while not gameover and ttl<150:
                    prev_points=points
                    costam, gameover, points = player(p, step)
                    if prev_points<points:
                        ttl=0
                    step+=1
                    ttl+=1
                print("fitness: ", p['eff'])

def init_layer(number_of_neurons, number_of_inputs_per_neuron):
    synaptic_weights = 2 * np.random.random((number_of_inputs_per_neuron, number_of_neurons)) - 1
    return synaptic_weights

def get_data(file_name):
    with open(file_name) as f:
        array = []
        arr = []
        points = 0
        with open('life_info') as pryta:
            life_info = pryta.read()
            if life_info == str(0):
                pryta.close()
                return array, arr, points, True
        temp1 = True
        for line in f:
            temp = [float(x) for x in line.split()]
            arr.append(temp)
            for x in temp:
                if x == float(1):
                    temp1=False
                if x == float(0.33):
                    points+=1  
                array.append(x)
        if temp1:
            points+=1
    f.close()
    if not array or not arr:
        input, arr, points, gameover = get_data(file_name)
        return input, arr, points, gameover
    else:
        return array, arr, points, False

def write_control(file_name,output,step):
    f = open(file_name, "w")
    if np.max(output) == output[0]:
        f.write("D " + str(step+1))
    elif np.max(output) == output[1]:
        f.write("S " + str(step+1))
    elif np.max(output) == output[2]:
        f.write("A " + str(step+1))
    elif np.max(output) == output[3]:
        f.write("W " + str(step+1))
    f.close()

def sigmoid(x):
    return 1/(1+np.exp(-x))

def relu(x):
    return np.maximum(0,x)

def think(X, layer1, layer2):
    W1 = layer1
    W2 = layer2
    output_1 = relu(np.dot(X, W1))
    output_2 = sigmoid(np.dot(output_1,W2))
    return output_2

def calculate_fitness(steps, points, distance):
    fitness = 10000 * points + 1 * steps + 1000//distance
    return fitness

def player(layers,step):
    input, arr, points, gameover = get_data("data")
    layer1 = layers["W1"]
    layer2 = layers["W2"]
    if not gameover:
        arr = np.array(arr, dtype=object)
        head_position = np.where(arr == 0.67)
        apple_position = np.where(arr == 1)
        try:
            distance = math.fabs(head_position[0]-apple_position[0]) + math.fabs(head_position[1]-apple_position[1])
        except TypeError:
            distance = 15
        layers["eff"] = calculate_fitness(step, points, distance)
    else:
        f = open("control", "w")
        f.write("0 0")
        f.close()
        f = open("life_info", "w")
        f.write("1")
        f.close()
        return layers, gameover, points
    output = think(input, layer1, layer2)
    write_control("control", output, step)
    return layers, gameover, points

def teach_me_senpai():
    ga = GeneticAlgorithm()
    ga.run()

teach_me_senpai()