import React, { useEffect, useState } from 'react';
import { useAuth } from './AuthContext.tsx';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import { Plus } from 'lucide-react';
import TaskForm from './TaskForm.tsx';

interface Task {
    id: number;
    title: string;
    description: string;
    status: string;
    deadline: string;
    assignedUserId: number;
}

const TaskList: React.FC = () => {
    const { token } = useAuth();
    const [tasks, setTasks] = useState<Task[]>([]);
    const [status, setStatus] = useState<string>('');
    const [assignedUserId, setAssignedUserId] = useState<string>('');
    const [searchTerm, setSearchTerm] = useState<string>('');
    const [showTaskForm, setShowTaskForm] = useState(false);

    useEffect(() => {
        fetchTasks();
    }, [status, assignedUserId]);

    const fetchTasks = async () => {
        try {
            let url = 'http://localhost:8080/tasks';
            const params = new URLSearchParams();
            if (status) params.append('status', status);
            if (assignedUserId) params.append('assignedUserId', assignedUserId);
            if (params.toString()) url += `?${params.toString()}`;

            const response = await axios.get(url, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setTasks(response.data);
        } catch (error) {
            console.error('Error fetching tasks:', error);
        }
    };

    const handleTaskAdded = () => {
        fetchTasks();
    };

    const filteredTasks = tasks.filter(task =>
        task.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
        task.description.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const getStatusBadgeClass = (status: string) => {
        switch (status) {
            case 'TODO':
                return 'badge bg-warning text-dark';
            case 'IN_PROGRESS':
                return 'badge bg-info text-dark';
            case 'DONE':
                return 'badge bg-success';
            default:
                return 'badge bg-secondary';
        }
    };

    return (
        <>
            <div className="container mt-4">
                <div className="d-flex justify-content-between align-items-center mb-4">
                    <h2>Task List</h2>
                    <button
                        className="btn btn-primary"
                        onClick={() => setShowTaskForm(true)}
                    >
                        <Plus size={20} className="me-2" />
                        Add Task
                    </button>
                </div>

                {/* Rest of the TaskList component remains the same */}
                <div className="card mb-4">
                    <div className="card-body">
                        <div className="row g-3">
                            <div className="col-md-4">
                                <input
                                    type="text"
                                    className="form-control"
                                    placeholder="Search tasks..."
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                />
                            </div>
                            <div className="col-md-4">
                                <select
                                    className="form-select"
                                    value={status}
                                    onChange={(e) => setStatus(e.target.value)}
                                >
                                    <option value="">All Statuses</option>
                                    <option value="TODO">TODO</option>
                                    <option value="IN_PROGRESS">IN PROGRESS</option>
                                    <option value="DONE">DONE</option>
                                </select>
                            </div>
                            <div className="col-md-4">
                                <input
                                    type="number"
                                    className="form-control"
                                    placeholder="Assigned User ID"
                                    value={assignedUserId}
                                    onChange={(e) => setAssignedUserId(e.target.value)}
                                />
                            </div>
                        </div>
                    </div>
                </div>

                <div className="table-responsive">
                    <table className="table table-hover">
                        <thead className="table-light">
                        <tr>
                            <th>Title</th>
                            <th>Description</th>
                            <th>Status</th>
                            <th>Deadline</th>
                            <th>Assigned User ID</th>
                        </tr>
                        </thead>
                        <tbody>
                        {filteredTasks.map((task) => (
                            <tr key={task.id}>
                                <td>{task.title}</td>
                                <td>{task.description}</td>
                                <td>
                                        <span className={getStatusBadgeClass(task.status)}>
                                            {task.status}
                                        </span>
                                </td>
                                <td>{new Date(task.deadline).toLocaleDateString()}</td>
                                <td>{task.assignedUserId}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                    {filteredTasks.length === 0 && (
                        <div className="text-center py-4 text-muted">
                            No tasks found
                        </div>
                    )}
                </div>
            </div>

            {showTaskForm && (
                <TaskForm
                    onClose={() => setShowTaskForm(false)}
                    onTaskAdded={handleTaskAdded}
                />
            )}
        </>
    );
};

export default TaskList;