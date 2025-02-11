import React, { useState } from 'react';
import { useAuth } from './AuthContext.tsx';
import axios from 'axios';

interface TaskFormProps {
    onClose: () => void;
    onTaskAdded: () => void;
}

const TaskForm: React.FC<TaskFormProps> = ({ onClose, onTaskAdded }) => {
    const { token } = useAuth();
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [deadline, setDeadline] = useState('');
    const [assignedUserId, setAssignedUserId] = useState('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await axios.post('http://localhost:8080/tasks', {
                title,
                description,
                deadline,
                status: 'TODO',
                assignedUserId: assignedUserId ? parseInt(assignedUserId) : null
            }, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            onTaskAdded();
            onClose();
        } catch (error) {
            console.error('Error adding task:', error);
            if (axios.isAxiosError(error) && error.response?.status === 401) {
                alert('Unauthorized: Please log in again');
            } else {
                alert('Failed to add task');
            }
        }
    };

    return (
        <div className="modal d-block" tabIndex={-1}>
            <div className="modal-dialog">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title">Add New Task</h5>
                        <button type="button" className="btn-close" onClick={onClose}></button>
                    </div>
                    <div className="modal-body">
                        <form onSubmit={handleSubmit}>
                            <div className="mb-3">
                                <label htmlFor="title" className="form-label">Title:</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    id="title"
                                    value={title}
                                    onChange={(e) => setTitle(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="mb-3">
                                <label htmlFor="description" className="form-label">Description:</label>
                                <textarea
                                    className="form-control"
                                    id="description"
                                    value={description}
                                    onChange={(e) => setDescription(e.target.value)}
                                    rows={3}
                                ></textarea>
                            </div>
                            <div className="mb-3">
                                <label htmlFor="deadline" className="form-label">Deadline:</label>
                                <input
                                    type="date"
                                    className="form-control"
                                    id="deadline"
                                    value={deadline}
                                    onChange={(e) => setDeadline(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="mb-3">
                                <label htmlFor="assignedUserId" className="form-label">Assigned User ID:</label>
                                <input
                                    type="number"
                                    className="form-control"
                                    id="assignedUserId"
                                    value={assignedUserId}
                                    onChange={(e) => setAssignedUserId(e.target.value)}
                                />
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" onClick={onClose}>Cancel</button>
                                <button type="submit" className="btn btn-primary">Add Task</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default TaskForm;