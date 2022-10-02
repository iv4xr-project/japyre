from qlearning   import Qlearning
from sqworld_env import SqWorldEnv
import gymenv_client

gymenv_client.debug = False
env = SqWorldEnv(6) # 6 is the size of the sq-world as set at the Java-side
N = env.size
N_plus_2 = N+2
qalg = Qlearning(N_plus_2*N_plus_2, len(env.javaGym.actionSpace))
#qalg.alpha = 1
print("====== Training...")
maxSteps = 2000
def convertToIndex(obs) :
    x = obs[0]
    y = obs[1]
    return ((x+1) * N_plus_2) + y +1
k = 0
stepCountInEpisode = 0
episode = 0
o = env.reset()
while k < maxSteps:
    o_ = convertToIndex(o)
    #print(f"### o={o}, o_={o_}")
    action = qalg.getNextAction(o_)
    #print(f"### action = {action}")
    nextObs,reward,done,i = env.step(action) 
    nextO_ = convertToIndex(nextObs)
    #print(f"### next={nextObs}, next_={nextO_}")
    qalg.applyReward(o_,action,nextO_,reward)
    debugo = o
    o = nextObs
    if done :
        print(f">> Episode {episode}, #steps={stepCountInEpisode}, reward={reward}")
        episode = episode + 1
        o = env.reset()
        stepCountInEpisode = 0
        #if (reward > 0) : 
        #    print(f"### o={debugo}, o_={o_}, next={nextObs}, next_={nextO_}, rw={reward}")
        #    print(f"### updated enrty={qalg.qtable[o_][action]}")
        #    break
        
        
    else:
        stepCountInEpisode = stepCountInEpisode + 1
    k = k + 1

print("====== Training has finished.")
for y in range(N_plus_2):
    y_ = N_plus_2 - y - 1
    y__ = y_ - 1
    for x in range(N_plus_2) :
        x__ = x - 1
        index = x * N_plus_2 + y_ 
        vals = qalg.qtable[index]
        print(f"q({x__},{y__})={vals}")

print("====== Demonstrating trained skill ...")
# now showing how to use the learned information
k = 0
done = False
o = env.reset()
print(f">> Robot initial position: {o}")    
while (not done) and k < 10 :
    o_ = convertToIndex(o)
    #print(f"### o={o}, o_={o_}")
    action = qalg.getNextTrainedAction(o_)
    #print(f"### action = {action}")
    nextObs,reward,done,i = env.step(action) 
    nextO_ = convertToIndex(nextObs)
    print(f">> Action {env.javaGym.actionSpace[action]}, new position: {nextObs}, reward={reward}, terminal={done}")    
    o = nextObs
    k = k + 1
print("======")
env.close()



